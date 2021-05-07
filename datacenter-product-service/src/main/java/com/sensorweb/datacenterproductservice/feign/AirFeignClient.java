package com.sensorweb.datacenterproductservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@FeignClient(value = "datacenter-air-service")
public interface AirFeignClient {

    @GetMapping(path = "getExportHBAirDataByIds")
    Map<String, String> getExportHBAirDataByIds(@RequestParam(value = "airQualityHours") List<Integer> ids);

    @GetMapping(path = "getExportTWAirDataByIds")
    Map<String, String> getExportTWAirDataByIds(@RequestParam(value = "twepas") List<Integer> ids);

    @GetMapping(path = "getExportCHAirDataByIds")
    Map<String, String> getExportCHAirDataByIds(@RequestParam(value = "chinaAirQualityHours") List<Integer> ids);
}
