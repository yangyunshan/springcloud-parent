package com.sensorweb.datacenterhimawariservice.dao;

import com.sensorweb.datacenterhimawariservice.entity.Himawari;
import com.sensorweb.datacenterhimawariservice.service.InsertHimawariService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface HimawariMapper {
    int insertData(Himawari himawari);
    int insertDataBatch(List<Himawari> himawaris);

    Himawari selectById(int id);
    Himawari selectByName(String name);
//    Himawari selectBySpatialAndTemporal(@Param("region") String region, @Param("begin") Instant begin, @Param("end") Instant end);
}
