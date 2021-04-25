package com.sensorweb.sosobsservice.controller;

import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import com.sensorweb.sosobsservice.entity.Observation;
import com.sensorweb.sosobsservice.service.GetObservationExpandService;
import com.sensorweb.sosobsservice.service.GetObservationService;
import com.sensorweb.sosobsservice.util.ObsConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
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
    @GetMapping(path = "getObservationCountOfMonth")
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

    @ApiOperation("查询所有的传感器数据量")
    @GetMapping(path = "getObservationNum")
    public int getObservationNum() {
        return getObservationExpandService.getAllObservationNum();
    }

    @ApiOperation("分页查询观测数据")
    @GetMapping(path = "getObservationByPage")
    public List<Observation> getObservationByPage(@ApiParam(name = "pageNum", value = "当前页码") @Param("pageNum") int pageNum,
                                                  @ApiParam(name = "pageSize", value = "每页的数据条目数") @Param("pageSize") int pageSize) {
        return getObservationExpandService.getObservationByPage(pageNum, pageSize);
    }

    @GetMapping("getObservationByConditions")
    public List<Observation> getObservationByConditions(String bbox, String timeBegin, String timeEnd, String type) {
        List<Observation> res = new ArrayList<>();
        try {
            LocalDateTime begin = DataCenterUtils.string2LocalDateTime(timeBegin);
            LocalDateTime end = DataCenterUtils.string2LocalDateTime(timeEnd);
            Instant start = begin.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
            Instant stop = end.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
            res = getObservationExpandService.getObservationByConditions(bbox, start, stop, type);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        return res;
    }

}
