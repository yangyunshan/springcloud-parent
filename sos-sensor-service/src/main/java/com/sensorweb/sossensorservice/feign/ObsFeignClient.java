package com.sensorweb.sossensorservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "sos-obs-service")
public interface ObsFeignClient {

    @DeleteMapping("observation/{id}")
    void deleteObservationById(@PathVariable("id") String procedureId);
}
