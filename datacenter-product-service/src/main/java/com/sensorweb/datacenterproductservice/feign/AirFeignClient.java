package com.sensorweb.datacenterproductservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sos-air-service")
public interface AirFeignClient {
    @GetMapping(path = "getExportHBAirDataByID")
    Map<String, String> getExportHBAirDataByID(@RequestParam(value = "ids") List<Integer> ids);

    @GetMapping(path = "getExportCHAirDataByID")
    Map<String, String> getExportCHAirDataByID(@RequestParam(value = "ids") List<Integer> ids);

    @GetMapping(path = "getExportTWAirDataByID")
    Map<String, String> getExportTWAirDataByID(@RequestParam(value = "ids") List<Integer> ids);
}
