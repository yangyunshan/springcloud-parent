package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Classification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ClassificationMapper {
    int insertData(Classification classification);

    int deleteById(int id);
    int deleteByProcedureId(String identifier);

    Classification selectById(int id);
    List<Classification> selectByLabelAndValue(@Param("label") String label, @Param("value") String value);
}
