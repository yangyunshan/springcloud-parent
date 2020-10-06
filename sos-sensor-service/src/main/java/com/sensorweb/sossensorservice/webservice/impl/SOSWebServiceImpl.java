package com.sensorweb.sossensorservice.webservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.sensorweb.sossensorservice.entity.sos.Observation;
import com.sensorweb.sossensorservice.service.sos.*;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import com.sensorweb.sossensorservice.webservice.SOSWebservice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vast.ows.sos.*;
import org.vast.ows.swe.DescribeSensorRequest;
import org.vast.ows.swe.InsertSensorResponseWriterV20;
import org.vast.xml.DOMHelper;
import org.w3c.dom.Element;

import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "SOS", targetNamespace = "http://webservice.datacenter.sensorweb.com/",
        endpointInterface = "com.sensorweb.sossensorservice.webservice.SOSWebservice")
@Component
public class SOSWebServiceImpl implements SOSWebservice {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private InsertObservationService insertObservationService;

    @Autowired
    private GetObservationService getObservationService;


    @Override
    public String InsertSensor(String requestContent) {
        String str = "";
        InsertSensorResponse response = new InsertSensorResponse();
        try {
            InsertSensorRequest request = insertSensorService.getInsertSensorRequest(requestContent);
            String result = insertSensorService.insertSensor(request);
            if (!StringUtils.isBlank(result)) {
                response.setAssignedProcedureId("");
                response.setAssignedProcedureId(result);
                InsertSensorResponseWriterV20 responseWriter = new InsertSensorResponseWriterV20();
                DOMHelper domHelper = new DOMHelper();
                Element element = responseWriter.buildXMLResponse(domHelper, response, "2.0");
                str = DataCenterUtils.element2String(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public String DescribeSensor(String requestContent) {
        String str = "";

        try {
            DescribeSensorRequest request = describeSensorService.getDescribeSensorRequest(requestContent);
            String procedureId = describeSensorService.getProcedureId(request);
            String descriptionFormat = describeSensorService.getDescriptionFormat(request);
            str = describeSensorService.getDescribeSensorResponse(procedureId, descriptionFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public String InsertObservation(String requestContent) {

        return "str";
    }

    @Override
    public String GetObservation(String requestContent) {
        try {
            GetObservationRequest request = getObservationService.getObservationRequest(requestContent);
            List<Observation> observations = getObservationService.getObservationContent(request);
            GetObservationResponse response = new GetObservationResponse();
            if (observations!=null && observations.size()>0) {
                for (Observation observation : observations) {
                    getObservationService.getObservationResponse(observation.getValue(), response);
                }
            }
            DOMHelper domHelper = new DOMHelper();
            GetObservationResponseWriter writer = new GetObservationResponseWriter();
            Element element = writer.buildXMLResponse(domHelper, response, "2.0");
            String test = DataCenterUtils.element2String(element);
            return DataCenterUtils.element2String(element);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Autowired
    private DescribeSensorExpandService expandService;
    /**
     * 根据查询条件查询procedure
     *
     * @param condition
     * @return
     */
    @Override
    public String SearchSensor(String condition) {
        return expandService.searchSensor(condition);
    }

    /**
     * 判断当前id是否已存在
     *
     * @param procedureId
     * @return
     */
    @Override
    public String isExist(String procedureId) {
        return expandService.isExist(procedureId);
    }

    /**
     * 通过文件地址获取文件内容
     *
     * @param filePath
     * @return
     */
    @Override
    public String getXMLContent(String filePath) {
        return expandService.getSensorContentById(filePath);
    }

    /**
     * 通过平台查传感器
     *
     * @param platformId
     * @return
     */
    @Override
    public String getComponent(String platformId) {
        List<String> componentIds = expandService.getComponentByPlatformId(platformId);
        return JSONObject.toJSONString(expandService.getSensorByIds(componentIds));
    }

    /**
     * 获取目录树
     *
     * @return
     */
    @Override
    public String getTOC() {
        return expandService.getTOC();
    }


}
