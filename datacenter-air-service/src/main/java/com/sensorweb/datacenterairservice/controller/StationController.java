package com.sensorweb.datacenterairservice.controller;

import com.netflix.discovery.converters.Auto;
import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHour;
import com.sensorweb.datacenterairservice.entity.TWEPA;
import com.sensorweb.datacenterairservice.service.InsertAirService;
import com.sensorweb.datacenterairservice.service.InsertChinaAirService;
import com.sensorweb.datacenterairservice.service.InsertTWEPA;
import com.sensorweb.datacenterairservice.util.AirConstant;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class StationController implements AirConstant {

    @Autowired
    private InsertAirService insertAirService;

    @Autowired
    private InsertChinaAirService insertChinaAirService;

    @Autowired
    private InsertTWEPA insertTWEPA;

    @GetMapping(value = "registryWHAirStation")
    public Map<String, String> registryWHAirStation() {
        Map<String, String> res = new HashMap<>();
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00").withZone(ZoneId.of("Asia/Shanghai"));
        String time = formatter.format(dateTime);
        try {
            String param = "UsrName=" + AirConstant.USER_NAME + "&passWord=" + AirConstant.PASSWORD +
                    "&date=" + URLEncoder.encode(time, "utf-8");
            String document = DataCenterUtils.doGet(AirConstant.GET_LAST_HOURS_DATA, param);
            List<Object> objects = insertAirService.parseXmlDoc(document);
            boolean flag = insertAirService.insertWHAirStationInfo(objects);
            if (flag) {
                res.put("status", "success");
            } else  {
                res.put("status", "failed");
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            res.put("status", "failed");
        }
        return res;
    }

    @GetMapping(value = "registryCHAirStation")
    public Map<String, String> registryCHAirStation() {
        Map<String, String> res = new HashMap<>();
        try {
            String document = insertChinaAirService.getApiDocument();
            if (!StringUtils.isBlank(document)) {
                List<ChinaAirQualityHour> chinaAirQualityHours = insertChinaAirService.getChinaAQHInfo(document);
                boolean flag = insertChinaAirService.insertCHAirStationInfo(chinaAirQualityHours);
                if (flag) {
                    res.put("status", "success");
                } else  {
                    res.put("status", "failed");
                }
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            res.put("status", "failed");
        }
        return res;
    }

    @GetMapping(value = "registryTWAirStation")
    public Map<String, String> registryTWAirStation() {
        Map<String, String> res = new HashMap<>();
        try {
            String document = insertTWEPA.getDocumentByGZip(insertTWEPA.downloadFile());
            if (!StringUtils.isBlank(document)) {
                List<TWEPA> twepas = insertTWEPA.getEPAInfo(document);
                boolean flag = insertTWEPA.insertTWAirStationInfo(twepas);
                if (flag) {
                    res.put("status", "success");
                } else  {
                    res.put("status", "failed");
                }
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            res.put("status", "failed");
        }
        return res;
    }
}
