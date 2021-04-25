package com.sensorweb.datacenterweatherservice.dao;

import com.sensorweb.datacenterweatherservice.entity.ChinaWeather;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface WeatherMapper {
    int insertData(ChinaWeather chinaWeather);
    int insertDataBatch(List<ChinaWeather> chinaWeathers);

    ChinaWeather selectById(int id);
}
