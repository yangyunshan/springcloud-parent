package com.sensorweb.datacenterairservice.dao;

import com.sensorweb.datacenterairservice.entity.TWEPA;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TWEPAMapper {
    int insertData(TWEPA twepa);
    int insertDataBatch(List<TWEPA> twepas);

    TWEPA selectById(int id);
}
