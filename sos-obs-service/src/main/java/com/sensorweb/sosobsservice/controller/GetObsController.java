package com.sensorweb.sosobsservice.controller;

import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import com.sensorweb.sosobsservice.entity.Observation;
import com.sensorweb.sosobsservice.service.GetObservationExpandService;
import com.sensorweb.sosobsservice.service.GetObservationService;
import com.sensorweb.sosobsservice.util.ObsConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Api("观测数据查询相关API")
@CrossOrigin
@RestController
@Slf4j
public class GetObsController implements ObsConstant {
    @Autowired
    private GetObservationExpandService getObservationExpandService;

    @ApiOperation("查询近一年各月的观测数据接入数量")
    @GetMapping(path = "observation/getObservationCountOfMonth")
    public List<Map<String, Integer>> getObservationCountOfMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        List<Map<String, Integer>> res = new ArrayList<>();
        calendar.add(Calendar.MONTH, -12);
        for (int i=0; i<12; i++) {
            int month = calendar.get(Calendar.MONTH);
            String begin = DataCenterUtils.getFirstDay(calendar);
            String end = DataCenterUtils.getLastDay(calendar);

            Instant start = DataCenterUtils.string2Instant(begin);
            Instant stop = DataCenterUtils.string2Instant(end);
            int count = getObservationExpandService.getObservationByTemporal(start, stop).size();

            Map<String, Integer> temp = new HashMap<>();
            temp.put(month+"", count);
            res.add(temp);
            calendar.add(Calendar.MONTH, 1);
        }
        return res;
    }

    @ApiOperation("查询所有的传感器数据")
    @GetMapping(path = "observation/getObservation")
    public List<Observation> getObservation() {
        return getObservationExpandService.getAllObservation();
    }

}
