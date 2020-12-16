package com.sensorweb.datacentercwicservice.dao;

import com.sensorweb.datacentercwicservice.entity.Record;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RecordMapper {
    int insertData(Record record);

    Record selectById(int id);
}
