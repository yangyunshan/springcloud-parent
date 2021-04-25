package com.sensorweb.datacenterproductservice.controller;

import com.sensorweb.datacenterproductservice.entity.Product;
import com.sensorweb.datacenterproductservice.service.InsertProductService;
import com.sensorweb.datacenterproductservice.service.InsertGenProductService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@Api("数据产品查询相关API")
public class InsertProductController {
    @Value("${datacenter.path.product}")
    private String targetDir;

    @Autowired
    private InsertProductService insertProductService;

    @PostMapping(value = "insertProduct")
    public void insertProduct(@RequestBody Product info) {
       insertProductService.InsertProduct(info);
    }

    @GetMapping(value = "registryProduct")
    public Map<String, String> registryProduct(@RequestParam("tmpDir") String tmpDir) {
        Map<String, String> res = new HashMap<>();
        insertProductService.moveFileByPath(tmpDir, targetDir);
        res.put("status", "success");
        return res;
    }
}
