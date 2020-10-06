package com.sensorweb.sosobsservice.service;

import com.sensorweb.sosobsservice.entity.air.AirQualityDay;
import com.sensorweb.sosobsservice.entity.air.AirQualityHour;
import com.sensorweb.sosobsservice.service.InsertObservationService;
import com.sensorweb.sosobsservice.util.DataCenterConstant;
import com.sensorweb.sosobsservice.util.DataCenterUtils;
import com.sensorweb.sosobsservice.util.SWEModelUtils;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataRecord;
import net.opengis.swe.v20.Quantity;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vast.data.QuantityImpl;
import org.vast.data.SWEFactory;
import org.vast.ogc.def.DefinitionRef;
import org.vast.ogc.gml.FeatureRef;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.IProcedure;
import org.vast.ogc.om.ObservationImpl;
import org.vast.ogc.om.ProcedureRef;
import org.vast.ogc.xlink.IXlinkReference;
import org.vast.ows.OWSException;
import org.vast.ows.sos.InsertObservationRequest;
import org.vast.ows.sos.SOSUtils;
import org.vast.util.TimeExtent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

@Service
@Configuration
@EnableScheduling
public class AirService implements DataCenterConstant {

    @Autowired
    InsertObservationService service;

    /**
     * 每小时接入一次数据
     */
    @Scheduled(cron = "0 30 0/1 * * ?")
    public void insertDataByHour() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00").withZone(ZoneId.of("Asia/Shanghai"));
        String time = formatter.format(dateTime);
        insertHourDataByHour(time);
        System.out.println(dateTime.toString());
    }

    /**
     * 每天接入一次数据
     */
    @Scheduled(cron = "0 30 0 * * ?")//每天的0:30分执行一次
    public void insertDataByDay() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        String endTime = dateTime.format(formatter);
        String beginTime = dateTime.minusDays(1).format(formatter);
        insertDayDataByDate(beginTime, endTime);
    }

    /**
     * 每周执行一次接入数据
     * @throws Exception
     */
//    @Scheduled(cron = "0 10 0 ? * MON")//，每周一的0点10分执行一次
    public void insertDataByWeek() throws Exception {
        insert7DaysData();
    }

    /**
     * 接入最近24小时内的数据
     * @throws Exception
     */
    public void insert24HoursData() throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
        String document = doGet(DataCenterConstant.GET_LAST_24_HOUR_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 接入最近7天内的数据
     * @throws Exception
     */
    public void insert7DaysData() throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD;
        String document = doGet(DataCenterConstant.GET_LAST_7_Days_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 根据时间接入指定时间的小时数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @throws Exception
     */
    public void insertHourDataByHour(String time) throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD +
                "&date=" + URLEncoder.encode(time, "utf-8");
        String document = doGet(DataCenterConstant.GET_LAST_HOURS_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 根据时间接入指定日期内的天数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @param beginTime/endTime格式: "2020-08-01 00:00:00", "2020-08-10 00:00:00"
     * @throws Exception
     */
    public void insertDayDataByDate(String beginTime, String endTime) throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD +
                "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") +
                "&endTime=" + URLEncoder.encode(endTime, "utf-8");
        String document = doGet(DataCenterConstant.GET_ORIGINAL_DAYILY_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    /**
     * 根据时间接入指定日期内的小时数据（当数据库中缺少某个时间段的数据时，可作为数据补充）
     * @throws Exception
     */
    public void insertHourDataByDate(String beginTime, String endTime) throws Exception {
        String param = "UsrName=" + DataCenterConstant.USER_NAME + "&passWord=" + DataCenterConstant.PASSWORD +
                "&beginTime=" + URLEncoder.encode(beginTime, "utf-8") +
                "&endTime=" + URLEncoder.encode(endTime, "utf-8");;
        String document = doGet(DataCenterConstant.GET_ORIGINAL_HOURLY_DATA, param);
        if (!StringUtils.isBlank(document)) {
            List<Object> objects = parseXmlDoc(document);
            InsertObservationRequest request = writeInsertObservationRequest(objects);
            List<IObservation> iObservations = service.getObservation(request);
            if (iObservations!=null && iObservations.size()>0) {
                for (IObservation iObservation : iObservations) {
                    service.insertObservation(iObservation);
                }
            }
        }
    }

    public String doGet(String url, String param) throws IOException {
        //打开postman
        //这一步相当于运行main方法。
        //创建request连接 3、填写url和请求方式
        HttpGet get = new HttpGet(url + "?" + param);
        //如果有参数添加参数 get请求不需要参数，省略
        CloseableHttpClient client = HttpClients.createDefault();
        //点击发送按钮，发送请求、获取响应报文
        CloseableHttpResponse response = client.execute(get);
        //格式化响应报文
        HttpEntity entity = response.getEntity();

        return EntityUtils.toString(entity);
    }

    /**
     * 向指定url发送post请求
     * @param url
     * @param param
     * @return
     */
    public String doPost(String url, String param) throws IOException {
        //打开postman
        //这一步相当于运行main方法。
        //创建request连接、填写url和请求方式
        HttpPost httpPost = new HttpPost(url);
        //额外设置Content-Type请求头
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
        //如果有参数添加参数
        CloseableHttpClient client = HttpClients.createDefault();
        httpPost.setEntity(new StringEntity(param,"UTF-8"));
        //点击发送按钮，发送请求、获取响应报文
        CloseableHttpResponse response = client.execute(httpPost);
        //格式化响应报文
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    /**
     * 获取IProcedure属性
     * @param o
     * @return
     */
    public IProcedure getProcedure(Object o) {
        IProcedure procedure = new ProcedureRef();
        String id = "";
        if (o instanceof AirQualityDay) {
           id = ((AirQualityDay) o).getUniqueCode();
        } else {
            id = ((AirQualityHour) o).getUniqueCode();
        }
        ((IXlinkReference)procedure).setHref(id);
        return procedure;
    }

    /**
     * 获取id属性
     * @param o
     * @return
     */
    public String getId(Object o) {
        String id = "";
        if (o instanceof AirQualityDay) {
            id = ((AirQualityDay) o).getUniqueCode();
        } else {
            id = ((AirQualityHour) o).getUniqueCode();
        }
        return id;
    }

    public DefinitionRef getObservedProperty(Object o) {
        DefinitionRef observedProperty = new DefinitionRef();
        String property = "";
        if (o instanceof AirQualityHour) {
            property = "AirQualityByHour";
        } else {
            property = "AirQualityByDay";
        }
        observedProperty.setHref(property);
        return observedProperty;
    }

    /**
     * 设置DataComponent属性，为Observation的Result
     *
     * @param object 数据模型为HourAqi、DayAqi、CDay、IDayAqi、IHourAqi
     * @return
     */
    public DataComponent setDataComponent(Object object) {
        SWEFactory factory = new SWEFactory();
        DataRecord dataRecord = factory.newDataRecord();
        writeResult(object, dataRecord);
        return dataRecord;
    }

    /**
     * 定义AirQualityHour数据模型字段
     * @param dataRecord
     * @param airQualityHour
     */
    public void writeAirQualityHourType(DataRecord dataRecord, AirQualityHour airQualityHour) {
        Quantity pm25OneHour = new QuantityImpl();
        Quantity pm10OneHour = new QuantityImpl();
        Quantity so2OneHour = new QuantityImpl();
        Quantity no2OneHour = new QuantityImpl();
        Quantity coOneHour = new QuantityImpl();
        Quantity o3OneHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        if (airQualityHour!=null) {
            SWEModelUtils.setQuantity(pm25OneHour, airQualityHour.getPm25OneHour(), "" ,"μg/m3");
            SWEModelUtils.setQuantity(pm10OneHour, airQualityHour.getPm10OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(so2OneHour, airQualityHour.getSo2OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(no2OneHour, airQualityHour.getNo2OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(coOneHour, airQualityHour.getCoOneHour(), "", "mg/m3");
            SWEModelUtils.setQuantity(o3OneHour, airQualityHour.getO3OneHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(aqi, airQualityHour.getAqi(), "", "");
        }
        //add pm25OneHour field
        pm25OneHour.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25OneHour);
        //add pm10OneHour field
        pm10OneHour.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10OneHour);
        //add SO2OneHour field
        so2OneHour.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2OneHour);
        //add no2OneHour field
        no2OneHour.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2OneHour);
        //add coOneHour field
        coOneHour.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", coOneHour);
        //add O3OneHour
        o3OneHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3OneHour);
    }

    /**
     * 定义AirQualityDay数据模型字段
     * @param dataRecord
     * @param airQualityDay
     */
    public void writeAirQualityDayType(DataRecord dataRecord, AirQualityDay airQualityDay) {
        Quantity pm25 = new QuantityImpl();
        Quantity pm10 = new QuantityImpl();
        Quantity so2 = new QuantityImpl();
        Quantity no2 = new QuantityImpl();
        Quantity co = new QuantityImpl();
        Quantity o3EightHour = new QuantityImpl();
        Quantity aqi = new QuantityImpl();
        if (airQualityDay!=null) {
            SWEModelUtils.setQuantity(pm25, airQualityDay.getPm25(), "" ,"μg/m3");
            SWEModelUtils.setQuantity(pm10, airQualityDay.getPm10(), "", "μg/m3");
            SWEModelUtils.setQuantity(so2, airQualityDay.getSo2(), "", "μg/m3");
            SWEModelUtils.setQuantity(no2, airQualityDay.getNo2(), "", "μg/m3");
            SWEModelUtils.setQuantity(co, airQualityDay.getCo(), "", "mg/m3");
            SWEModelUtils.setQuantity(o3EightHour, airQualityDay.getO3EightHour(), "", "μg/m3");
            SWEModelUtils.setQuantity(aqi, airQualityDay.getAqi(), "", "");
        }
        //add pm25OneHour field
        pm25.setDefinition("Pm25OneHour");
        dataRecord.addField("Pm25OneHour", pm25);
        //add pm10OneHour field
        pm10.setDefinition("PM10OneHour");
        dataRecord.addField("PM10OneHour", pm10);
        //add SO2OneHour field
        so2.setDefinition("SO2OneHour");
        dataRecord.addField("SO2OneHour", so2);
        //add no2OneHour field
        no2.setDefinition("NO2OneHour");
        dataRecord.addField("NO2OneHour", no2);
        //add coOneHour field
        co.setDefinition("COOneHour");
        dataRecord.addField("COOneHour", co);
        //add O3EightHour
        o3EightHour.setDefinition("O3OneHour");
        dataRecord.addField("O3OneHour", o3EightHour);
    }

    /**
     * 写结果属性,使用DataRecord方式
     * @param object
     * @param dataRecord
     */
    public void writeResult(Object object, DataRecord dataRecord) {
        if (object!=null) {
            if (object instanceof AirQualityHour) {
                writeAirQualityHourType(dataRecord, (AirQualityHour) object);
            } else {
                writeAirQualityDayType(dataRecord, (AirQualityDay) object);
            }
        }
    }


    /**
     * 根据不同模型，获取唯一id
     * @param object
     * @return
     */
    public String getUniqueCode(Object object) {
        String res = null;
        if (object instanceof AirQualityDay) {
            res = ((AirQualityDay) object).getUniqueCode();
        } else {
            res = ((AirQualityHour) object).getUniqueCode();
        }
        return res;
    }

    /**
     * 根据不同的数据模型，获取对应的时间数据
     * @param object
     * @return
     */
    public Instant getRusltTime(Object object) {
        Instant res = null;
        if (object instanceof AirQualityDay) {
            res = ((AirQualityDay) object).getTime();
        } else {
            res = ((AirQualityHour) object).getTime();
        }
        return res;
    }

    /**
     * 根据不同的数据模型，获取对应的现象时间数据
     * @param object
     * @return
     */
    public TimeExtent getPhenomenonTime(Object object) {
        Instant end = getRusltTime(object);
        Instant begin = null;
        if (object instanceof AirQualityHour) {
            begin = end.minusSeconds(60*60);
        } else {
            begin = end.minusSeconds(24*60*60);
        }
        if (end!=null && begin!=null) {
            return TimeExtent.period(begin, end);
        } else {
            return null;
        }
    }

    /**
     * 根据不同的数据模型，获取对应的FOI数据
     * @param object
     * @return
     */
    public IGeoFeature getFeatureOfInterset(Object object) {
        String name = "";
        String id = "";
        if (object instanceof AirQualityHour) {
            name = ((AirQualityHour) object).getStationName();
            id = ((AirQualityHour) object).getUniqueCode();
        } else {
            name = ((AirQualityDay) object).getStationName();
            id = ((AirQualityDay) object).getUniqueCode();
        }
        FeatureRef<?> ref = new FeatureRef<>();
        ref.setHref(name);
        return ref;

//        SamplingFeature<AbstractGeometry> res = new SamplingFeature<>();
//        res.setUniqueIdentifier(id);
//        res.setName(name);
//        return res;
    }
    /**
     * 根据Object对象，首先判断参数对象属于哪种数据模型，然后封装成IObservation对象进行返回
     * @param objects
     * @return
     */
    public List<IObservation> getObservation(List<Object> objects) throws UnsupportedEncodingException {
        List<IObservation> res = new ArrayList<>();

        if (objects!=null && objects.size()>0) {
            for (Object object : objects) {
                ObservationImpl observation = new ObservationImpl();
                observation.setId(getId(object));
                observation.setUniqueIdentifier(getId(object));
                observation.setProcedure(getProcedure(object));
                observation.setResultTime(getRusltTime(object));
                observation.setPhenomenonTime(getPhenomenonTime(object));
                observation.setFeatureOfInterest(getFeatureOfInterset(object));
                observation.setPhenomenonTime(getPhenomenonTime(object));
                observation.setObservedProperty(getObservedProperty(object));
                observation.setType("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement");
                observation.setResult(setDataComponent(object));
                res.add(observation);
            }
        }
        return res;
    }

    /**
     * 获取Request请求对象
     * @param objects
     * @return
     * @throws OWSException
     * @throws UnsupportedEncodingException
     */
    public InsertObservationRequest writeInsertObservationRequest(List<Object> objects) throws UnsupportedEncodingException {
        InsertObservationRequest request = new InsertObservationRequest();
        SOSUtils.loadRegistry();
        request.setVersion("2.0");
        request.setService(SOSUtils.SOS);
        request.getObservations().addAll(getObservation(objects));
        return request;
    }

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
                airQualityHour.setTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
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
                airQualityHour.setO3OneHour(attribute.getText().equals("NA")||StringUtils.isBlank(attribute.getText()) ? -1+"":attribute.getText());
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
                airQualityDay.setTime(DataCenterUtils.string2LocalDateTime2(attribute.getText()).toInstant(ZoneOffset.ofHours(+8)));
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
