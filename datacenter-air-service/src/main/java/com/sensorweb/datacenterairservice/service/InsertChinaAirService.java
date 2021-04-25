package com.sensorweb.datacenterairservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sensorweb.datacenterairservice.dao.AirQualityHourMapper;
import com.sensorweb.datacenterairservice.dao.AirStationMapper;
import com.sensorweb.datacenterairservice.dao.ChinaAirQualityHourMapper;
import com.sensorweb.datacenterairservice.entity.*;
import com.sensorweb.datacenterairservice.feign.ObsFeignClient;
import com.sensorweb.datacenterairservice.feign.SensorFeignClient;
import com.sensorweb.datacenterairservice.util.AirConstant;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
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
public class InsertChinaAirService extends Thread implements AirConstant {

    @Autowired
    private AirStationMapper airStationMapper;

    @Autowired
    private ChinaAirQualityHourMapper chinaAirQualityHourMapper;

    @Autowired
    private SensorFeignClient sensorFeignClient;

    @Autowired
    private ObsFeignClient obsFeignClient;

    /**
     * 每小时接入一次数据
     */
    @Scheduled(cron = "0 15 0/1 * * ?") //每个小时的05分开始接入
    public void insertChinaDataByHour() {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00").withZone(ZoneId.of("Asia/Shanghai"));
        String time = formatter.format(dateTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = false;
                try {
                    String str = getApiDocument();
                    flag = insertDataByHour(getChinaAQHInfo(str));
                    if (flag) {
                        log.info("全国空气质量监测站接入时间: " + dateTime.toString() + "Status: Success");
                        System.out.println("全国空气质量监测站接入时间: " + dateTime.toString() + "Status: Success");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.info("全国空气质量监测站接入时间: " + dateTime.toString() + "Status: Fail");
                    System.out.println("全国空气质量监测站接入时间: " + dateTime.toString() + "Status: Fail");
                }
            }
        }).start();
    }


    /**
     * 根据时间接入指定时间的小时数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean insertDataByHour(List<ChinaAirQualityHour> chinaAirQualityHours) {
        //将环境监测站数据
        for (int i = 0; i < chinaAirQualityHours.size(); i++) {
            int status = chinaAirQualityHourMapper.insert(chinaAirQualityHours.get(i));
            if (status>0) {
                Observation observation = new Observation();
                observation.setProcedureId(chinaAirQualityHours.get(i).getStationCode());
                observation.setObsTime(str2Instant(chinaAirQualityHours.get(i).getTimePoint()));
                observation.setMapping("china_2020");
                observation.setObsProperty("AirQuality");
                observation.setType("ch_air");
                observation.setEndTime(observation.getObsTime());
                observation.setBeginTime(observation.getObsTime());
                observation.setOutId(chinaAirQualityHours.get(i).getId());
                boolean flag = true;
//                try {
//                    flag = sensorFeignClient.isExist(observation.getProcedureId());
//                    System.out.println(flag);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                if (flag) {
                    System.out.println(observation.getProcedureId() + " :is existed");
                    obsFeignClient.insertData(observation);
                } else {
                    log.info("procedure:" + observation.getProcedureId() + "不存在");
                }
            }
        }
        return true;
    }

    /**
     * 字符串转Instant  yyyy-MM-dd'T'HH:mm:ss'Z'
     * @return
     * @throws IOException
     */
    public Instant str2Instant(String time) {
        String pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        dateTimeFormatter.withZone(ZoneId.of("Asia/Shanghai"));
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
    }

    public String getApiDocument() throws IOException {
        String param = "token=" + AirConstant.token;
        return DataCenterUtils.doGet(AirConstant.Get_All_Country_Hourly_DATA, param);
    }

    public List<ChinaAirQualityHour> getChinaAQHInfo(String document) {
        List<ChinaAirQualityHour> res = new ArrayList<>();
        if (!StringUtils.isBlank(document)) {
            JSONArray jsonObjects = JSON.parseArray(document);
            if (jsonObjects!=null) {
                for (int i=0; i<jsonObjects.size(); i++) {
                    ChinaAirQualityHour chinaAirQualityHour = new ChinaAirQualityHour();
                    JSONObject feed = jsonObjects.getJSONObject(i);
                    chinaAirQualityHour.setAqi(feed.getString("aqi"));
                    chinaAirQualityHour.setArea(feed.getString("area"));
                    chinaAirQualityHour.setCo(feed.getString("co"));
                    chinaAirQualityHour.setCo24h(feed.getString("co_24h"));
                    chinaAirQualityHour.setNo2(feed.getString("no2"));
                    chinaAirQualityHour.setNo224h(feed.getString("no2_24h"));
                    chinaAirQualityHour.setO3(feed.getString("o3"));
                    chinaAirQualityHour.setO324h(feed.getString("o3_24h"));
                    chinaAirQualityHour.setO38h(feed.getString("o3_8h"));
                    chinaAirQualityHour.setO38h24h(feed.getString("o3_8h_24h"));
                    chinaAirQualityHour.setPm10(feed.getString("pm10"));
                    chinaAirQualityHour.setPm1024h(feed.getString("pm10_24h"));
                    chinaAirQualityHour.setPm25(feed.getString("pm2_5"));
                    chinaAirQualityHour.setPm2524h(feed.getString("pm2_5_24h"));
                    chinaAirQualityHour.setPositionName(feed.getString("position_name"));
                    chinaAirQualityHour.setPrimaryPollutant(feed.getString("primary_pollutant"));
                    chinaAirQualityHour.setQuality(feed.getString("quality"));
                    chinaAirQualityHour.setSo2(feed.getString("so2"));
                    chinaAirQualityHour.setSo224h(feed.getString("so2_24h"));
                    chinaAirQualityHour.setStationCode(feed.getString("station_code"));
                    chinaAirQualityHour.setStationCode(feed.getString("time_point"));
                    res.add(chinaAirQualityHour);
                }
            }
        }
        return res;
    }

    /**
     * 将全国空气站点注册到数据库
     * @param
     */
    public boolean insertCHAirStationInfo(List<ChinaAirQualityHour> chinaAirQualityHours) throws IOException {
        List<AirStationModel> res = new ArrayList<>();
        if (chinaAirQualityHours!=null && chinaAirQualityHours.size()>0) {
            for (ChinaAirQualityHour chinaAirQualityHour : chinaAirQualityHours) {
                AirStationModel airStationModel = new AirStationModel();
                airStationModel.setStationId(chinaAirQualityHour.getStationCode());
                airStationModel.setStationName(chinaAirQualityHour.getPositionName());
                airStationModel.setRegion(chinaAirQualityHour.getArea());
                JSONObject jsonObject = null;
                if (airStationModel.getStationName()!=null) {
                    jsonObject = getGeoAddress(airStationModel.getStationName());
                }
                if (jsonObject!=null) {
                    Object result = jsonObject.get("result");
                    if (result!=null) {
                        Object location = ((JSONObject) result).get("location");
                        airStationModel.setLon(((JSONObject) location).getDoubleValue("lng"));
                        airStationModel.setLat(((JSONObject) location).getDoubleValue("lat"));
                    }
                }
                airStationModel.setStationType("ch_air");
                res.add(airStationModel);
            }
        }
        int status = airStationMapper.insertDataBatch(res);
        return status > 0;
    }

    /**
     * 根据地址获取经纬度信息,转换成JSON对象返回
     * @param address
     */
    public JSONObject getGeoAddress(String address) throws IOException {
        String param = "address=" + URLEncoder.encode(address, "utf-8") + "&output=json" + "&ak=" + AirConstant.BAIDU_AK;
        String document = DataCenterUtils.doGet(AirConstant.BAIDU_ADDRESS_API, param);
        if (document!=null) {
            return JSON.parseObject(document);
        }
        return null;
    }

}


