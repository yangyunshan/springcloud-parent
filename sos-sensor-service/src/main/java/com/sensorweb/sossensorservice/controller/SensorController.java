package com.sensorweb.sossensorservice.controller;

import com.sensorweb.sossensorservice.entity.sos.Observation;
import com.sensorweb.sossensorservice.service.sos.*;
import com.sensorweb.sossensorservice.util.DataCenterConstant;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import com.sensorweb.sossensorservice.service.sos.GetObservationExpandService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.vast.ows.sos.InsertSensorRequest;
import org.vast.xml.XMLReaderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api
@RestController
public class SensorController implements DataCenterConstant {

    @Autowired
    private InsertSensorService insertSensorService;

    @Autowired
    private DescribeSensorService describeSensorService;

    @Autowired
    private DeleteSensorService deleteSensorService;

    private static Logger LOGGER = LoggerFactory.getLogger(SensorController.class);

    /**
     * 通过SensorML文件批量注册传感器
     * @param files
     * @return
     * @throws Exception
     */
    @Operation(summary = "通过SensorML文件批量注册传感器")
    @PostMapping(path = "/registryByFiles")
    public List<Map<String, String>> insertSensor(@Parameter(description = "SensorML文件") @RequestParam("files") MultipartFile[] files) throws Exception {
        List<Map<String, String>> res = new ArrayList<>();
        if (files!=null && files.length>0) {
            for (int i=0; i< files.length; i++) {
                Map<String, String> map = new HashMap<>();
                MultipartFile file = files[i];
                String temp = DataCenterUtils.readFromMultipartFile(file);
                String content = temp.substring(temp.indexOf("<sml"));
                InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
                String flag = "";
                try {
                    flag = insertSensorService.insertSensor(request);
                } catch (Exception e) {
                    flag = "";
                }
                if (StringUtils.isBlank(flag)) {
                    map.put(file.getName(), "failed");
                } else {
                    map.put(file.getName(),"success");
                }
                res.add(map);
            }
        }
        return res;
    }

    /**
     * 通过SensorML xml内容进行注册（非批量）
     * @param temp
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/registryByContent")
    public Map<String, String> insertSensorByContent(@Parameter(description = "SensorML xml内容") @RequestParam("xmlContent") String temp) throws Exception {
        Map<String, String> res = new HashMap<>();
        String content = temp.substring(temp.indexOf("<sml"));
        InsertSensorRequest request = insertSensorService.getInsertSensorRequest(DataCenterConstant.INSERT_SENSOR_PREFIX + content + DataCenterConstant.INSERT_SENSOR_SUFFIX);
        String flag;
        try {
            flag = insertSensorService.insertSensor(request);
        } catch (Exception e) {
            flag = "";
        }
        if (StringUtils.isBlank(flag)) {
            res.put("status","failed");
        } else {
            res.put("status", "success");
        }
        return res;
    }

    /**
     * 获取系统中已注册传感器信息
     */
    @Autowired
    private DescribeSensorExpandService describeSensorExpandService;

    @GetMapping(path = "/getAllProcedureInfo")
    public String getAllProcedure() {
//        String info = describeSensorExpandService.getTOC();
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
