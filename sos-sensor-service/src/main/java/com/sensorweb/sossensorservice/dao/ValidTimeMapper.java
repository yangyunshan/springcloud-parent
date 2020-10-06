package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.ValidTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
@Mapper
public interface ValidTimeMapper {
    int insertData(ValidTime validTime);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    String selectById(int id);
    List<String> selectByTime(@Param("begin") Instant beginTime, @Param("end") Instant endTime);
    List<String> selectByBeginTime(Instant begin);
    List<String> selectByEndTime(Instant end);
}
