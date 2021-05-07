package com.sensorweb.datacenterweatherservice.dao;

import com.sensorweb.datacenterweatherservice.entity.ChinaWeather;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface WeatherMapper {
    int insertData(ChinaWeather chinaWeather);
    int insertDataBatch(List<ChinaWeather> chinaWeathers);

    ChinaWeather selectById(int id);
    List<ChinaWeather> selectByTemporal(@Param("begin") Instant begin, @Param("end") Instant end);

}
