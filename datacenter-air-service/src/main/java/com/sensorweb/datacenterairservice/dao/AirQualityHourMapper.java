package com.sensorweb.datacenterairservice.dao;

import com.sensorweb.datacenterairservice.entity.AirQualityHour;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AirQualityHourMapper {
    int insertData(AirQualityHour airQualityHour);
    int insertDataBatch(List<AirQualityHour> airQualityHours);

    AirQualityHour selectById(int id);
}
