package com.sensorweb.sossensorservice.controller;

import com.sensorweb.sossensorservice.entity.Platform;
import com.sensorweb.sossensorservice.service.DescribeSensorExpandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    @GetMapping(path = "getAllProcedureInfo")
    public List<Platform> getAllProcedure() {
        //获取Header中的信息
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String ss = request.getHeader("user");
        return describeSensorExpandService.getTOC();
    }

    /**
     * 查询procedure是否存在
     */
    @ApiOperation("判断传感器或平台是否存在")
    @GetMapping("isExist/{id}")
    public boolean isExist(@ApiParam(name = "procedureId", value = "传感器或平台的id") @PathVariable("id") String procedureId) {
        return describeSensorExpandService.isExist(procedureId);
    }
}
