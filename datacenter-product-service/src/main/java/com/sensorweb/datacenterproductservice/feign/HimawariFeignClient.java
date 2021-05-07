package com.sensorweb.datacenterproductservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Service
@FeignClient(value = "datacenter-himawari-service")
public interface HimawariFeignClient {
    @GetMapping("getHimawariDataById")
    public Map<String, String> getHimawariDataById(@RequestParam("id") int id);
}
