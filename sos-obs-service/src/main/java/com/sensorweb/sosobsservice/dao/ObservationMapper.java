package com.sensorweb.sosobsservice.dao;

import com.sensorweb.sosobsservice.entity.sos.Observation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
@Mapper
public interface ObservationMapper {
    int insertData(Observation observation);

    int deleteByProcedureId(String procedureId);

    List<Observation> selectObservationsByConditions(@Param("procedureIds") Set<String> procedureIds,
                                                     @Param("observedProperties") Set<String> observedProperties,
                                                     @Param("begin") Instant begin,
                                                     @Param("end") Instant end);

    List<Observation> selectObservationsBySpatial(@Param("minX") double minX, @Param("minY") double minY,
                                                  @Param("maxX") double maxX, @Param("maxY") double maxY);

    List<Observation> selectObservationsBySensorId(String sensorId);

    int selectObservationsByDateTime(@Param("begin") Instant begin, @Param("end") Instant end);
}
