package com.sensorweb.sosobsservice.dao;

import com.sensorweb.sosobsservice.entity.sos.FeatureOfInterest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface FoiMapper {
    int insertData(FeatureOfInterest foi);

    int deleteById(String id);
    int deleteByName(String name);
    int deleteByProcedureId(String procedureId);

    FeatureOfInterest selectById(String id);
    //判断是否在多边形内
    boolean selectByIdAndGeom(@Param("id") String id, @Param("polygon") String polygon);
    boolean isExist(String id);
}
