package com.sensorweb.eureka_client;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@Api
@RequestMapping("/test")
public class Test {
    @Operation(summary = "just for test")
    @GetMapping("/one")
    public Map<String, String> getMsg(@Parameter(description = "test-param") int id) {
        Map<String, String> res = new HashMap<>();
        res.put("1","msg1");
        res.put("2","msg2");
        return res;
    }
}
