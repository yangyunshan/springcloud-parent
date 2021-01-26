package com.sensorweb.datacenterhimawariservice.dao;

import com.sensorweb.datacenterhimawariservice.entity.Himawari;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface HimawariMapper {
    int insertData(Himawari himawari);
    int insertDataBatch(List<Himawari> himawaris);

    Himawari selectById(int id);
    Himawari selectByName(String name);
}
