package com.sensorweb.datacenterproductservice.feign;

import com.sensorweb.datacenterproductservice.entity.Observation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "sos-obs-service")
public interface ObsFeignClient {

    @DeleteMapping("delete/{id}")
    int deleteObservationById(@PathVariable("id") String procedureId);

    @GetMapping("getObservationByConditions")
    List<Observation> getObservationByConditions(@RequestParam("bbox") String bbox, @RequestParam("timeBegin") String timeBegin,
                                                 @RequestParam("timeEnd") String timeEnd, @RequestParam("type") String type);

}
