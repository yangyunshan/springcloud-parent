package com.sensorweb.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class UserController {
    @Value("${spring.cloud}")
    private String config;

    @GetMapping("/test/config")
    public String test() {
        return config;
    }
}
