package com.sensorweb.datacentercwicservice.feign;

import com.sensorweb.datacentercwicservice.entity.Observation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "sos-obs-service")
public interface ObsFeignClient {

    @DeleteMapping("observation/{id}")
    int deleteObservationById(@PathVariable("id") String procedureId);

    @PostMapping("observation/insertData")
    int insertData(@RequestBody Observation observation);

    @PostMapping("observation/insertDataBatch")
    int insertDataBatch(@RequestBody List<Observation> observations);
}
