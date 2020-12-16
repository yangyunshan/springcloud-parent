package com.sensorweb.sosobsservice.controller;

import com.sensorweb.sosobsservice.entity.Observation;
import com.sensorweb.sosobsservice.service.InsertObservationService;
import com.sensorweb.sosobsservice.util.ObsConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.vast.ogc.om.IObservation;
import org.vast.ows.sos.InsertObservationRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api("观测数据注册相关API")
@CrossOrigin
@Controller
public class InsertObsController implements ObsConstant {
    @Autowired
    private InsertObservationService insertObservationService;

    @ApiOperation("通过OM观测数据文档,接入观测数据")
    @PostMapping(path = "observation/insertObservation")
    public Map<String, Object> insertObservation(@ApiParam(name = "content", value = "观测数据xml文档内容") String content) {
        Map<String, Object> res = new HashMap<>();
        if (StringUtils.isBlank(content)) {
            res.put("status", "failed");
        } else {
            String temp = INSERT_OBSERVATION_PREFIX + content + INSERT_OBSERVATION_SUFFIX;
            try {
                InsertObservationRequest request = insertObservationService.getInsertObservationRequest(temp);
                List<IObservation> observations = request.getObservations();
                if (observations!=null && observations.size()>0) {
                    for (IObservation observation :observations) {
                        insertObservationService.insertObservation(observation);
                    }
                    res.put("status", "success");
                }
            } catch (Exception e) {
                res.put("status", "failed");
                log.info(e.getMessage());
            }
        }
        return res;
    }

    @ApiOperation("插入自定义Observation对象数据")
    @PostMapping("observation/insertData")
    public Map<String, Object> insertData(@ApiParam(name = "observation", value = "自定义Observation对象") @RequestBody Observation observation) {
        Map<String, Object> res = new HashMap<>();
        boolean flag = insertObservationService.insertObservationData(observation);
        if (flag) {
            res.put("status", true);
        } else {
            res.put("status", false);
        }
        return res;
    }
}
