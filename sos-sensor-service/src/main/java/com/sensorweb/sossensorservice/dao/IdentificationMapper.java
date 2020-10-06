package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Identification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface IdentificationMapper {
    int insertData(Identification identification);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    Identification selectById(int id);
    Identification selectByLabelAndProcedureId(@Param("label") String label, @Param("procedureId") String procedureId);
    List<Identification> selectByLabelAndValue(@Param("label") String label, @Param("value") String value);
}
