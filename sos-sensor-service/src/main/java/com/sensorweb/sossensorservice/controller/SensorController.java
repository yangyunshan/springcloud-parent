package com.sensorweb.sossensorservice.controller;

import com.sensorweb.sossensorservice.entity.sos.Observation;
import com.sensorweb.sossensorservice.service.sos.*;
import com.sensorweb.sossensorservice.util.DataCenterConstant;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import com.sensorweb.sossensorservice.service.sos.GetObservationExpandService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.xml.XMLReaderException;

import java.util.List;

@Controller
@RequestMapping(path = "/sensor")
public class SensorController implements DataCenterConstant {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private DeleteSensorService deleteSensorService;

    private static Logger LOGGER = LoggerFactory.getLogger(SensorController.class);

    /**
     * 批量注册传感器/procedure
     * @param model
     * @param files
     * @return
     * @throws Exception
     */
    @RequestMapping(path = "/registry", method = RequestMethod.POST)
    public String insertSensor(Model model, @RequestParam("files") MultipartFile[] files) throws Exception {
        if (files!=null && files.length>0) {
            for (int i=0; i< files.length; i++) {
                MultipartFile file = files[i];
                String temp = DataCenterUtils.readFromMultipartFile(file);
                String content = temp.substring(temp.indexOf("<sml"));
                InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
                insertSensorService.insertSensor(request);
            }
        }

        return "html/register";
    }

    @RequestMapping(path = "/registryByContent", method = RequestMethod.POST)
    public String insertSensorByContent(@RequestParam("xmlContent") String temp) throws Exception {
        if (!StringUtils.isBlank(temp)) {
            String content = temp.substring(temp.indexOf("<sml"));
            InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
            insertSensorService.insertSensor(request);
        }

        return "html/register";
    }

    /**
     * 通过XML文档注册传感器，不支持批量
     */
    @RequestMapping(path = "registryByXML", method = RequestMethod.POST)
    public String insertSensor(Model model, String xmlContent) throws Exception {
        if (!StringUtils.isBlank(xmlContent)) {
            String content = xmlContent.substring(xmlContent.indexOf("<sml"));
            InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
            insertSensorService.insertSensor(request);
            model.addAttribute("msg", "registry success");
        } else {
            model.addAttribute("msg", "registry failed");
        }
        return "index";
    }

    /**
     * 获取系统中已注册传感器信息
     */
    @Autowired
    private DescribeSensorExpandService describeSensorExpandService;

    @RequestMapping(path = "getAllProcedureInfo", method = RequestMethod.GET)
    public String getAllProcedure(Model model) {
        String info = describeSensorExpandService.getTOC();
        model.addAttribute("infos", info);
        return "html/sensor";
    }

    /**
     * 传感器查询
     */
    @RequestMapping(path = "getProcedureByCondition", method = RequestMethod.POST)
    public String getProcedureByCondition(Model model, String condition) {
        if (StringUtils.isBlank(condition)) {
            String res = describeSensorExpandService.searchSensor(condition);
            model.addAttribute("procedure", res);
        } else {
            model.addAttribute("procedure", null);
        }
        return "html/sensor";
    }

    /**
     * 通过传感器查询观测数据
     */
    @Autowired
    private GetObservationExpandService getObservationExpandService;

    @RequestMapping(path = "getObservationOfSensor", method = RequestMethod.GET)
    public String getObservationOfSensor(Model model, String id) throws XMLReaderException {
        List<Observation> observations = getObservationExpandService.getObservationInfo(id);
        model.addAttribute("observations", observations);
        return "html/sensor";
    }

}
