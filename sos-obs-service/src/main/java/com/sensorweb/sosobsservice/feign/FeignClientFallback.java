package com.sensorweb.sosobsservice.feign;

import org.springframework.stereotype.Component;

@Component
public class FeignClientFallback implements FeignClientTest {
    @Override
    public String getInfo(String name) {
        return "Sorry....";
    }
}
