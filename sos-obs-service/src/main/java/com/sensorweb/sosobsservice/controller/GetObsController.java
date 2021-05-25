package com.sensorweb.sosobsservice.controller;

import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import com.sensorweb.sosobsservice.dao.ObservationMapper;
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

    @Autowired
    private ObservationMapper observationMapper;

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

    @GetMapping(path = "getObservationByConditions")
    public List<Observation> getObservationByConditions(@RequestParam("dataType") String dataType, @RequestParam("ranSpa") String ranSpa,
                                                        @RequestParam("timeBegin") Instant timeBegin, @RequestParam("timeEnd") Instant timeEnd) {

        if (ranSpa.equals("武汉城市圈")) {
            List<Integer> cityIds = observationMapper.selectCityIdInWuCityCircle();
            if (cityIds!=null && cityIds.size()>0) {
                return observationMapper.selectByTemAndSpaAndType(timeBegin, timeEnd, dataType, ranSpa, cityIds);
            }
        } else if (ranSpa.equals("全国")) {
            List<Integer> cityIds = observationMapper.selectCityIdInChina();
            if (cityIds!=null && cityIds.size()>0) {
                return observationMapper.selectByTemAndSpaAndType(timeBegin, timeEnd, dataType, ranSpa, cityIds);
            }
        } else if (ranSpa.equals("长江经济带")) {
            List<Integer> cityIds = observationMapper.selectCityIdInChangjiang();
            if (cityIds!=null && cityIds.size()>0) {
                return observationMapper.selectByTemAndSpaAndType(timeBegin, timeEnd, dataType, ranSpa, cityIds);
            }
        }
        return null;
    }



}
