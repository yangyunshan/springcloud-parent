package com.sensorweb.datacenterairservice.dao;

import com.sensorweb.datacenterairservice.entity.AirQualityHour;
import com.sensorweb.datacenterairservice.entity.TWEPA;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface TWEPAMapper {
    int insertData(TWEPA twepa);
    int insertDataBatch(List<TWEPA> twepas);

    TWEPA selectById(int id);
    List<TWEPA> selectByTemporal(@Param("begin") Instant begin, @Param("end") Instant end);
}
