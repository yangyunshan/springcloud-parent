package com.sensorweb.datacenterhimawariservice.dao;

import com.sensorweb.datacenterhimawariservice.entity.Himawari;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface HimawariMapper {
    int insertData(Himawari himawari);

    Himawari selectById(int id);
}
