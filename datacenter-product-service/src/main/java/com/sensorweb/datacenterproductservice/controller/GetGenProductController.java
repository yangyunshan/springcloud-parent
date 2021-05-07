package com.sensorweb.datacenterproductservice.controller;

import com.sensorweb.datacenterproductservice.entity.GenProduct;
import com.sensorweb.datacenterproductservice.entity.Observation;
import com.sensorweb.datacenterproductservice.feign.AirFeignClient;
import com.sensorweb.datacenterproductservice.feign.HimawariFeignClient;
import com.sensorweb.datacenterproductservice.feign.ObsFeignClient;
import com.sensorweb.datacenterproductservice.feign.WeatherFeignClient;
import com.sensorweb.datacenterproductservice.service.GetGenProductService;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@Slf4j
public class GetGenProductController {

    @Autowired
    private GetGenProductService getGenProductService;

    @Autowired
    private ObsFeignClient obsFeignClient;

    @Autowired
    private AirFeignClient airFeignClient;

    @Autowired
    private HimawariFeignClient himawariFeignClient;

    @Autowired
    private WeatherFeignClient weatherFeignClient;

    @GetMapping(value = "getGenProductById")
    public GenProduct getById(@Param("id") int id) {
        return getGenProductService.getGenProductById(id);
    }

    @GetMapping(value = "getGenProductByType")
    public GenProduct getById(@Param("type") String type) {
        return getGenProductService.getGenProductByType(type);
    }

    @GetMapping(value = "getGenProducts")
    public List<GenProduct> getById() {
        return getGenProductService.getGenProducts();
    }

    /**
     * 通过给出的参数，查询出相应产品类型所需要的所有数据的路径（本地路径）
     * @param productType 产品类型
     * @param spaRes 空间分辨率
     * @param ranType 范围类型
     * @param ranSpa 空间范围
     * @param timeRes 时间分辨率
     * @param timeBegin 开始时间
     * @param timeEnd 结束时间
     * @return
     */
    @GetMapping(value = "getFilePath")
    public Map<String, String> getFilePath(@RequestParam("productType") String productType, @RequestParam("spaRes") String spaRes,
                           @RequestParam("ranType") String ranType, @RequestParam("ranSpa") String ranSpa,
                           @RequestParam("timeRes") String timeRes, @RequestParam("timeBegin") String timeBegin,
                           @RequestParam("timeEnd") String timeEnd) {
        GenProduct genProduct = getGenProductService.getGenProductByType(productType);
        String[] dataTypes = genProduct.getDataNeeded().split(",");
        for (String dataType:dataTypes) {
            try {
                LocalDateTime start = string2LocalDateTime3(timeBegin);
                Instant begin = start.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
                LocalDateTime end = string2LocalDateTime3(timeEnd);
                Instant stop = end.atZone(ZoneId.of("Asia/Shanghai")).toInstant();
                Map<String, List<Observation>> timeObservations = new HashMap<>();
                if (timeRes.equals("1小时")) {
                    while (begin.isBefore(stop)) {
                        List<Observation> observations = obsFeignClient.getObservationByConditions(dataType, ranSpa, begin, begin.plusSeconds(60*60));
                        timeObservations.put(begin.toString(), observations);
                        begin = begin.plusSeconds(60*60);
                    }
                }
                return getData(timeObservations, dataType);
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Integer> getOutIdByObsList(List<Observation> obs) {
        List<Integer> res = new ArrayList<>();
        if (obs!=null && obs.size()>0) {
            for (Observation o:obs) {
                res.add(o.getOutId());
            }
        }
        return res;
    }

    public Map<String, String> getData(Map<String, List<Observation>> timeObservations, String dataType) {
        Map<String, String> res = new HashMap<>();
        if (timeObservations!=null && timeObservations.size()>0) {
            Set<String> keys = timeObservations.keySet();
            for (String key:keys) {
                List<Integer> ids = getOutIdByObsList(timeObservations.get(key));
                if (dataType.equals("HB_AIR")) {
                    res.put(key, airFeignClient.getExportHBAirDataByIds(ids).get("filePath"));
                } else if (dataType.equals("CH_AIR")) {
                    res.put(key, airFeignClient.getExportCHAirDataByIds(ids).get("filePath"));
                } else if (dataType.equals("TW_EPA_AIR")) {
                    res.put(key, airFeignClient.getExportTWAirDataByIds(ids).get("filePath"));
                } else if (dataType.equals("CH_WEATHER")) {
                    res.put(key, weatherFeignClient.getExportWeatherDataByIds(ids).get("filePath"));
                } else if (dataType.equals("Himawari")) {
                    res.put(key, himawariFeignClient.getHimawariDataById(ids.get(0)).get("result"));
                }
            }
        }
        return res;
    }

    public static LocalDateTime string2LocalDateTime3(String time) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        dateTimeFormatter.withZone(ZoneId.of("Asia/Shanghai"));
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return localDateTime;
    }
}
