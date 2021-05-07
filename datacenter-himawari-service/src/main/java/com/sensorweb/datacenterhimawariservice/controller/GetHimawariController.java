package com.sensorweb.datacenterhimawariservice.controller;

import com.sensorweb.datacenterhimawariservice.dao.HimawariMapper;
import com.sensorweb.datacenterhimawariservice.service.InsertHimawariService;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class GetHimawariController {

    @Autowired
    private HimawariMapper himawariMapper;

    @Autowired
    private InsertHimawariService insertHimawariService;

    /**
     * 获取数据
     * @param datetime yyyy-MM-ddTHH:mm:ss
     * @return
     */
    @GetMapping(value = "getHimawariData")
    public Map<String, String> getHimawariData(String datetime) {

        Map<String, String> res = new HashMap<>();
        try {
            LocalDateTime time = DataCenterUtils.string2LocalDateTime(datetime);
            boolean flag = insertHimawariService.insertData(time);
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

    @GetMapping("getHimawariDataById")
    public Map<String, String> getHimawariDataById(@RequestParam("id") int id) {
        Map<String, String> res = new HashMap<>();
        res.put("result", himawariMapper.selectById(id).getLocalPath());
        return res;
    }
}
