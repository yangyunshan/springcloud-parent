package com.sensorweb.datacenterairservice.service;

import com.sensorweb.datacenterairservice.dao.AirQualityHourMapper;
import com.sensorweb.datacenterairservice.entity.*;
import com.sensorweb.datacenterairservice.feign.ObsFeignClient;
import com.sensorweb.datacenterairservice.feign.SensorFeignClient;
import com.sensorweb.datacenterairservice.util.AirConstant;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class InsertAirService extends Thread implements AirConstant {
    @Autowired
    private AirQualityHourMapper airQualityHourMapper;

    @Autowired
    private SensorFeignClient sensorFeignClient;

    @Autowired
    private ObsFeignClient obsFeignClient;
    /**
     * 每小时接入一次数据
     */
    @Scheduled(cron = "0 20 0/1 * * ?") //每个小时的20分开始接入
    public void insertDataByHour() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00").withZone(ZoneId.of("Asia/Shanghai"));
        String time = formatter.format(dateTime);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    try {
                        flag = !insertHourDataByHour(time);
                        if (!flag) {
                            log.info("湖北省监测站接入时间: " + dateTime.toString() + "Status: Success");
                            System.out.println("湖北省监测站接入时间: " + dateTime.toString() + "Status: Success");
                        } else {
                            System.out.println("等待中...");
                        }
                        Thread.sleep(2 * 60 * 1000);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        log.info("湖北省监测站接入时间: " + dateTime.toString() + "Status: Fail");
                        System.out.println("湖北省监测站接入时间: " + dateTime.toString() + "Status: Fail");
                        break;
                    }
                }
            }
        }).start();
    }

    /**
     * 根据时间接入指定时间的小时数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @throws Exception
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean insertHourDataByHour(String time) throws Exception {
        String param = "UsrName=" + AirConstant.USER_NAME + "&passWord=" + AirConstant.PASSWORD +
                "&date=" + URLEncoder.encode(time, "utf-8");
        String document = DataCenterUtils.doGet(AirConstant.GET_LAST_HOURS_DATA, param);
        List<Object> objects = parseXmlDoc(document);
        if (objects.size()==0) {
            return false;
        }
        //将环境监测站数据
        for (Object o : objects) {
            if (o instanceof AirQualityHour) {
                AirQualityHour airQualityHour = (AirQualityHour)o;
                int status = airQualityHourMapper.insertData(airQualityHour);
                if (status>0) {
                    System.out.println("接入Air_Quality_Hourly成功：" + airQualityHour.getStationName());
                    Observation observation = new Observation();
                    observation.setProcedureId(airQualityHour.getUniqueCode());
                    observation.setObsTime(airQualityHour.getQueryTime());
                    observation.setMapping("air_quality_hourly");
                    observation.setObsProperty("AirQuality");
                    observation.setType("xml");
                    observation.setEndTime(getResultTime(airQualityHour));
                    observation.setBeginTime(getResultTime(airQualityHour).minusSeconds(60*60));
                    observation.setOutId(airQualityHour.getId());
                    boolean flag = false;
                    try {
                        flag = sensorFeignClient.isExist(observation.getProcedureId());
                        System.out.println(flag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (flag) {
                        System.out.println(observation.getProcedureId() + " :is existed");
                        obsFeignClient.insertData(observation);
                        System.out.println("接入Observation成功：" + airQualityHour.getStationName());
                    } else {
                        log.info("procedure:" + observation.getProcedureId() + "不存在");
//                        throw new Exception("procedure: " + observation.getProcedureId() + "不存在");
                    }
                }
            } else {
            }
        }
        return true;
    }

    /**
     * 根据不同的数据模型，获取对应的时间数据
     * @param object
     * @return
     */
    public Instant getResultTime(Object object) {
        Instant res = null;
        if (object instanceof AirQualityDay) {
            res = ((AirQualityDay) object).getQueryTime();
        } else {
            res = ((AirQualityHour) object).getQueryTime();
        }
        return res;
    }

    /**
     * 解析湖北省监测站响应的xml文档
     * @param document
     * @return
     * @throws Exception
     */
    public List<Object> parseXmlDoc(String document) throws Exception {
        //读取xml文档，返回Document对象
        Document xmlContent = DocumentHelper.parseText(document);
        Element root = xmlContent.getRootElement();
        String type = root.getName().substring(root.getName().lastIndexOf("_")+1);
        List<Object> objects = new ArrayList<>();
        //判断返回数据类型，选择对应的数据模型
        switch (type) {
            case "HourAqiModel":
            case "IHourAqiModel": {
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getHourAqiInfo(element));
                }
                break;
            }
            case "DayAqiModel":
            case "IDayAqiModel":
            case "CDayModel": {
                if (!root.elementIterator().hasNext()) {
                    return objects;
                }
                for (Iterator i = root.elementIterator(); i.hasNext();) {
                    Element element = (Element) i.next();
                    objects.add(getDayAqiInfo(element));
                }
                break;
            }
        }
        return objects;
    }

    /**
     * 解析Element要素节点，获取相关信息，包装成AirQualityHour模型返回
     * @param hourAqiElement
     * @return
     */
    public AirQualityHour getHourAqiInfo(Element hourAqiElement) throws ParseException {
        AirQualityHour airQualityHour = new AirQualityHour();
        for (Iterator j = hourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                airQualityHour.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime")) {
                airQualityHour.setQueryTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                airQualityHour.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25OneHour")) {
                airQualityHour.setPm25OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10OneHour")) {
                airQualityHour.setPm10OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2OneHour")) {
                airQualityHour.setSo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2OneHour")) {
                airQualityHour.setNo2OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("COOneHour")) {
                airQualityHour.setCoOneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3OneHour")) {
                airQualityHour.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                airQualityHour.setAqi(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PrimaryEP")) {
                airQualityHour.setPrimaryEP(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQDegree")) {
                airQualityHour.setAqDegree(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQType")) {
                airQualityHour.setAqType(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
            }
        }
        return airQualityHour;
    }

    /**
     * 解析Element要素节点，获取相关信息，包装成AirQualityDay模型返回
     * @param hourAqiElement
     * @return
     */
    public AirQualityDay getDayAqiInfo(Element hourAqiElement) throws ParseException {
        AirQualityDay airQualityDay = new AirQualityDay();
        for (Iterator j = hourAqiElement.elementIterator(); j.hasNext();) {
            Element attribute = (Element) j.next();
            if (attribute.getName().equals("UniqueCode")) {
                airQualityDay.setUniqueCode(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("QueryTime") || attribute.getName().equals("SDateTime")) {
                airQualityDay.setQueryTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
                continue;
            }
            if (attribute.getName().equals("StationName")) {
                airQualityDay.setStationName(attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM25")) {
                airQualityDay.setPm25(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("PM10")) {
                airQualityDay.setPm10(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("SO2")) {
                airQualityDay.setSo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("NO2")) {
                airQualityDay.setNo2(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("CO")) {
                airQualityDay.setCo(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("O3EightHour")) {
                airQualityDay.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
                continue;
            }
            if (attribute.getName().equals("AQI")) {
                airQualityDay.setO3EightHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
            }
        }
        return airQualityDay;
    }

    /**
     * 将有用地信息输出到文本文件
     * @param objects
     */
    public void writeInfo(List<Object> objects) {
        StringBuilder sb = new StringBuilder();
        if (objects!=null && objects.size()>0) {
            if (objects.get(0) instanceof AirQualityHour) {
                for (Object object : objects) {
                    AirQualityHour airQualityHour = (AirQualityHour) object;
                    sb.append("UniqueCode: ").append(airQualityHour.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(airQualityHour.getStationName()).append("\t");
                    sb.append("\n");
                }
            } else if (objects.get(0) instanceof AirQualityDay) {
                for (Object object : objects) {
                    AirQualityDay airQualityDay = (AirQualityDay) object;
                    sb.append("UniqueCode: ").append(airQualityDay.getUniqueCode()).append("\t");
                    sb.append("StationName: ").append(airQualityDay.getStationName()).append("\t");
                    sb.append("\n");
                }
            }
        }

        DataCenterUtils.write2File("/home/yangyunshan/StationInfo.txt", sb.toString());
    }
}
