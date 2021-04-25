package com.sensorweb.datacenterweatherservice.controller;

import com.sensorweb.datacenterweatherservice.dao.WeatherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
public class WeatherController {

    @Autowired
    private WeatherMapper weatherMapper;

    @GetMapping("getLAADSDataById")
    public Map<String, Object> getLAADSDataById(@RequestParam("id") int id) {
        Map<String, Object> res = new HashMap<>();
        res.put("result", weatherMapper.selectById(id));
        return res;
    }
}
