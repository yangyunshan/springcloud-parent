package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Procedure;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ProcedureMapper {
    int insertData(Procedure procedure);

    int deleteById(String id);

    Procedure selectById(String id);
    Procedure selectByIdAndFormat(@Param("id") String id, @Param("descriptionFormat") String descriptionFormat);
    List<String> selectAll();
    List<String> selectAllSensorIds();
    List<String> selectAllPlatformIds();

    boolean isExist(String procedureId);

}
