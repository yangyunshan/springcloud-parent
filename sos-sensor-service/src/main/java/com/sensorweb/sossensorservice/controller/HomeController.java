package com.sensorweb.sossensorservice.controller;

import com.sensorweb.sossensorservice.service.sos.DescribeSensorExpandService;
import com.sensorweb.sossensorservice.service.sos.GetObservationExpandService;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Controller
public class HomeController {

    @RequestMapping(path = "index")
    public String jump2Index() {
        return "index";
    }

    @RequestMapping(path = "sensor")
    public String jump2service() {
        return "html/sensor";
    }

    @RequestMapping(path = "observation")
    public String jump2Observation() {
        return "html/observation";
    }


    @RequestMapping(path = "register")
    public String jump2register() {
        return "html/register";
    }


    @Autowired
    private DescribeSensorExpandService describeSensorExpandService;

    @RequestMapping(path = "getTOC", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getAllProcedureInfo() {
        Map<String, String> res = new HashMap<>();
        String info = describeSensorExpandService.getTOC();
        if (!StringUtils.isBlank(info)) {
            res.put("allProcedureInfo", info);
        } else {
            res.put("allProcedureInfo", null);
        }
        return res;
    }

    /**
     * 获取全部传感器信息
     * @return
     */
    @RequestMapping(path = "getAllSensorInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAllSensorInfo() {
        Map<String, Object> res = new HashMap<>();
        List<Map<String,Object>> info = describeSensorExpandService.getAllSensorInfo();
        if (info!=null) {
            res.put("data", info);
            res.put("totals", info.size());
        }
        return res;
    }

    @Autowired
    private GetObservationExpandService getObservationExpandService;

    @RequestMapping(path = "getObservationByMonth", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Integer>> getObservationByMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        List<Map<String, Integer>> res = new ArrayList<>();
        calendar.add(Calendar.MONTH, -11);
        for (int i=0; i<12; i++) {
            int month = calendar.get(Calendar.MONTH)+1;
            String begin = DataCenterUtils.getFirstDay(calendar);
            String end = DataCenterUtils.getLastDay(calendar);

            Instant start = DataCenterUtils.string2Instant(begin);
            Instant stop = DataCenterUtils.string2Instant(end);
            int count = getObservationExpandService.getObservationByDateTime(start, stop);

            Map<String, Integer> temp = new HashMap<>();
            temp.put(month+"", count);
            res.add(temp);
            calendar.add(Calendar.MONTH, 1);
        }
        return res;
    }

    @RequestMapping(path = "getObservationByDay", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Integer>> getObservationByDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("Asia/Shanghai")));
        List<Map<String, Integer>> res = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = calendar.getMaximum(Calendar.DAY_OF_MONTH);
        for (int i=1; i<maxDay; i++) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            String begin = year + "-" + (month<10?"0"+month:month) + "-" + (i<10?"0"+i:i)+" 00:00:00";
            String end = year + "-" + (month<10?"0"+month:month) + "-" + (i<10?"0"+i:i)+" 23:59:59";
            Instant start = DataCenterUtils.string2Instant(begin);
            Instant stop = DataCenterUtils.string2Instant(end);

            int count = getObservationExpandService.getObservationByDateTime(start, stop);
            Map<String, Integer> temp = new HashMap<>();
            temp.put(i+"", count);
            res.add(temp);
        }
        return res;
    }
}

