package com.sensorweb.datacenterairservice.dao;

import com.sensorweb.datacenterairservice.entity.AirQualityHour;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AirQualityHourMapper {
    int insertData(AirQualityHour airQualityHour);

    AirQualityHour selectById(int id);
}
