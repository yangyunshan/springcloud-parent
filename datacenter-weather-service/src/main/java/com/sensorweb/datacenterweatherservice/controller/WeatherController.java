package com.sensorweb.datacenterweatherservice.controller;

import com.sensorweb.datacenterweatherservice.dao.WeatherMapper;
import com.sensorweb.datacenterweatherservice.entity.ChinaWeather;
import com.sensorweb.datacenterweatherservice.service.GetWeatherInfo;
import com.sensorweb.datacenterweatherservice.service.InsertWeatherInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
public class WeatherController {

    @Autowired
    private WeatherMapper weatherMapper;

    @Autowired
    private GetWeatherInfo getWeatherInfo;

    @GetMapping("getWeatherById")
    public Map<String, Object> getWeatherById(@RequestParam("id") int id) {
        Map<String, Object> res = new HashMap<>();
        res.put("result", weatherMapper.selectById(id));
        return res;
    }

    @GetMapping(path = "getExportWeatherDataByIds")
    public Map<String, String> getExportWeatherDataIds(@RequestParam(value = "ids") List<Integer> ids) {
        Map<String, String> res = new HashMap<>();
        List<ChinaWeather> chinaWeathers = new ArrayList<>();
        if (ids!=null && ids.size()>0) {
            for (Integer id:ids) {
                ChinaWeather chinaWeather = weatherMapper.selectById(id);
                chinaWeathers.add(chinaWeather);
            }
        }
        String filePath = getWeatherInfo.exportTXT(chinaWeathers);
        res.put("filePath", filePath);
        return res;
    }
}
