package com.sensorweb.sossensorservice.controller;

import com.sensorweb.sossensorservice.entity.Platform;
import com.sensorweb.sossensorservice.service.DescribeSensorExpandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api("传感器查询相关API")
@Slf4j
@CrossOrigin
public class GetSensorController {
    @Autowired
    private DescribeSensorExpandService describeSensorExpandService;

    /**
     * 获取系统中已注册传感器信息
     */
    @ApiOperation("获取所有的传感器信息,包括传感器和平台信息")
    @GetMapping(path = "/sensor/getAllProcedureInfo")
    public List<Platform> getAllProcedure() {
        return describeSensorExpandService.getTOC();
    }

    /**
     * 查询procedure是否存在
     */
    @ApiOperation("判断传感器或平台是否存在")
    @GetMapping("/sensor/isExist")
    public boolean isExist(@ApiParam(name = "procedureId", value = "传感器或平台的id") String procedureId) {
        return describeSensorExpandService.isExist(procedureId);
    }
}
