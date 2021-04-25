package com.sensorweb.datacenterairservice.controller;


import com.sensorweb.datacenterairservice.dao.AirQualityHourMapper;
import com.sensorweb.datacenterairservice.dao.ChinaAirQualityHourMapper;
import com.sensorweb.datacenterairservice.dao.TWEPAMapper;
import com.sensorweb.datacenterairservice.entity.AirQualityHour;
import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHour;
import com.sensorweb.datacenterairservice.entity.TWEPA;
import com.sensorweb.datacenterairservice.service.GetAirService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class GetAirQualityHour {

    @Autowired
    GetAirService getAirService;

    @Autowired
    private AirQualityHourMapper airQualityHourMapper;

    @Autowired
    private TWEPAMapper twepaMapper;

    @Autowired
    private ChinaAirQualityHourMapper chinaAirQualityHourMapper;

    @GetMapping(value = "getAllAirQualityHourly")
    @ResponseBody
    public Map<String, List<AirQualityHour>> getAllInfo() {
        Map<String, List<AirQualityHour>> res = new HashMap<>();
        List<AirQualityHour> info =  getAirService.getAll();
        res.put("Info", info);
        return res;
    }

    @ApiOperation("分页查询观测数据")
    @GetMapping(path = "getAirQualityHourlyByPage")
    public Map<String, List<AirQualityHour>> getAirQualityHourByPage(@ApiParam(name = "pageNum", value = "当前页码") @Param("pageNum") int pageNum,
                                                  @ApiParam(name = "pageSize", value = "每页的数据条目数") @Param("pageSize") int pageSize) {
        Map<String, List<AirQualityHour>> res = new HashMap<>();
        List<AirQualityHour> info =  getAirService.getAirQualityHourlyByPage(pageNum, pageSize);
        res.put("Info", info);
        return res;

    }


    @ApiOperation("id的分页查询")
    @GetMapping(path = "getAirQualityHourlyByID")
    public Map<String, List<AirQualityHour>> getAirQualityHourByPage(@RequestParam(value = "uniquecode") List<String> uniquecode,@ApiParam(name = "pageNum", value = "当前页码") @Param("pageNum") int pageNum,
                                                                     @ApiParam(name = "pageSize", value = "每页的数据条目数") @Param("pageSize") int pageSize) {
        Map<String, List<AirQualityHour>> res = new HashMap<>();
        List<AirQualityHour> info =  getAirService.getAirQualityHourlyByID(uniquecode,pageNum,pageSize);
        res.put("Info", info);
        return res;

    }


    @ApiOperation("id的分页查询数量number")
    @GetMapping(path = "getAirQualityHourlyNumberByID")
    public int getAirQualityHourlyNumberByID(@RequestParam(value = "uniquecode") List<String> uniquecode) {
        return getAirService.getAirQualityHourlyNumberByID(uniquecode);

    }

    @ApiOperation("查询气象数据数据量")
    @GetMapping(path = "getAirQualityHourNumber")
    public int getObservationNum() {
        return getAirService.getAirQualityHourNum();
    }

    @GetMapping(path = "getExportHBAirDataByID")
    public Map<String, String> getExportHBAirDataByID(@RequestParam(value = "ids") List<Integer> ids) {
        Map<String, String> res = new HashMap<>();
        List<AirQualityHour> airQualityHours = new ArrayList<>();
        if (ids!=null) {
            for (Integer id:ids) {
                AirQualityHour info =  airQualityHourMapper.selectById(id);
                airQualityHours.add(info);
            }
        }
        String filePath = getAirService.exportTXT_WH(airQualityHours);

        res.put("filePath", filePath);
        return res;
    }

    @GetMapping(path = "getExportTWAirDataByID")
    public Map<String, String> getExportTWAirDataByID(@RequestParam(value = "ids") List<Integer> ids) {
        Map<String, String> res = new HashMap<>();
        List<TWEPA> twepas = new ArrayList<>();
        if (ids!=null) {
            for (Integer id:ids) {
                TWEPA info =  twepaMapper.selectById(id);
                twepas.add(info);
            }
        }
        String filePath = getAirService.exportTXT_TW(twepas);

        res.put("filePath", filePath);
        return res;
    }

    @GetMapping(path = "getExportCHAirDataByID")
    public Map<String, String> getExportCHAirDataByID(@RequestParam(value = "ids") List<Integer> ids) {
        Map<String, String> res = new HashMap<>();
        List<ChinaAirQualityHour> chinaAirQualityHours = new ArrayList<>();
        if (ids!=null) {
            for (Integer id:ids) {
                ChinaAirQualityHour info =  chinaAirQualityHourMapper.selectById(id);
                chinaAirQualityHours.add(info);
            }
        }
        String filePath = getAirService.exportTXT_CH(chinaAirQualityHours);

        res.put("filePath", filePath);
        return res;
    }


}
