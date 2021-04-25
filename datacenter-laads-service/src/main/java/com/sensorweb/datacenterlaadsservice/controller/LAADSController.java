package com.sensorweb.datacenterlaadsservice.controller;

import com.sensorweb.datacenterlaadsservice.dao.EntryMapper;
import com.sensorweb.datacenterlaadsservice.service.InsertLAADSService;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class LAADSController {

    @Autowired
    private EntryMapper entryMapper;

    @Autowired
    private InsertLAADSService insertLaadsService;

    @GetMapping(value = "getHimawariData")
    public Map<String, String> getHimawariData(String satellite, String product, String startTime, String endTime, String bbox) {

        Map<String, String> res = new HashMap<>();
        try {
            boolean flag = insertLaadsService.insertData(satellite, startTime, endTime, bbox, product);
            if (flag) {
                res.put("status", "success");
            } else {
                res.put("status", "failed");
            }
        } catch (Exception e) {
            res.put("status", "failed");
        }
        return res;
    }

    @GetMapping("getLAADSDataById")
    public Map<String, Object> getLAADSDataById(@RequestParam("id") int id) {
        Map<String, Object> res = new HashMap<>();
        res.put("result", entryMapper.selectById(id));
        return res;
    }

}
