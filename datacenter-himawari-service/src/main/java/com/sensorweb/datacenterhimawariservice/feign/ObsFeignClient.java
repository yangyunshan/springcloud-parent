package com.sensorweb.datacenterhimawariservice.feign;

import com.sensorweb.datacenterhimawariservice.entity.Observation;
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
    void deleteObservationById(@PathVariable("id") String procedureId);

    @PostMapping("observation/insertData")
    Map<String, Object> insertData(@RequestBody Observation observation);

    @PostMapping("observation/insertDataBatch")
    int insertDataBatch(@RequestBody List<Observation> observations);
}
