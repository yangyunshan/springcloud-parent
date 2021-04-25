package com.sensorweb.datacenterairservice.dao;

import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHour;
import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHourExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface ChinaAirQualityHourMapper {
    int countByExample(ChinaAirQualityHourExample example);

    int deleteByExample(ChinaAirQualityHourExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ChinaAirQualityHour record);

    ChinaAirQualityHour selectById(int id);

    int insertSelective(ChinaAirQualityHour record);

    List<ChinaAirQualityHour> selectByExample(ChinaAirQualityHourExample example);

    ChinaAirQualityHour selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ChinaAirQualityHour record, @Param("example") ChinaAirQualityHourExample example);

    int updateByExample(@Param("record") ChinaAirQualityHour record, @Param("example") ChinaAirQualityHourExample example);

    int updateByPrimaryKeySelective(ChinaAirQualityHour record);

    int updateByPrimaryKey(ChinaAirQualityHour record);
}