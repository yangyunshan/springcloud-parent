package com.sensorweb.datacenterproductservice.controller;

import com.sensorweb.datacenterproductservice.entity.GenProduct;
import com.sensorweb.datacenterproductservice.entity.Observation;
import com.sensorweb.datacenterproductservice.feign.AirFeignClient;
import com.sensorweb.datacenterproductservice.feign.ObsFeignClient;
import com.sensorweb.datacenterproductservice.service.GetGenProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetGenProductController {

    @Autowired
    private GetGenProductService getGenProductService;

    @Autowired
    private ObsFeignClient obsFeignClient;

    @Autowired
    private AirFeignClient airFeignClient;

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
     * @return
     */
    @GetMapping(value = "getFilePath")
    public Map<String, String> getFilePath(@RequestParam("productType") String productType, @RequestParam("spaRes") String spaRes,
                           @RequestParam("ranType") String ranType, @RequestParam("ranSpa") String ranSpa,
                           @RequestParam("timeRes") String timeRes, @RequestParam("timeBegin") String timeBegin,
                           @RequestParam("timeEnd") String timeEnd) {
        Map<String, String> res = new HashMap<>();
        GenProduct genProduct = getGenProductService.getGenProductByType(productType);
        List<Observation> obs = obsFeignClient.getObservationByConditions(ranSpa, timeBegin, timeEnd, genProduct.getDataNeeded());
        List<Observation> temps = new ArrayList<>();
        if (obs.size()>0) {
            for (Observation o:obs) {
                if (genProduct.getDataNeeded().equals(o.getType())) {
                    temps.add(o);
                }
            }
        }
        List<Integer> hbAirIds = new ArrayList<>();
        List<Integer> chWeatherIds = new ArrayList<>();
        List<Integer> chAirIds = new ArrayList<>();
        List<Integer> twepaIds = new ArrayList<>();
        List<Integer> modIds = new ArrayList<>();
        List<Integer> himawariIds = new ArrayList<>();
        if (temps.size()>0) {
            for (Observation temp:temps) {
                String type = temp.getType();
                if (type.equals("hb_air")) {
                    hbAirIds.add(temp.getId());
                } else if (type.equals("ch_weather")) {
                    chWeatherIds.add(temp.getId());
                } else if (type.equals("ch_air")) {
                    chAirIds.add(temp.getId());
                } else if (type.equals("tw_epa_air")) {
                    twepaIds.add(temp.getId());
                } else if (type.startsWith("mod")) {
                    modIds.add(temp.getId());
                } else if (type.equals("himawari")) {
                    himawariIds.add(temp.getId());
                }
            }
        }
//        res.put(temp.getObsTime().toString(), airFeignClient.getExportHBAirDataByID(hbAirIds));
        return res;
    }
}
