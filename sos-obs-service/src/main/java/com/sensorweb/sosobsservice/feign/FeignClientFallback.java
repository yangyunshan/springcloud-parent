package com.sensorweb.sosobsservice.feign;

import org.springframework.stereotype.Component;

@Component
public class FeignClientFallback implements FeignClientTest {
    @Override
    public String getAllProcedure() {
        return "Sorry....";
    }
}
