package com.sensorweb.sossensorservice.dao;

import com.sensorweb.sossensorservice.entity.sos.Keyword;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Mapper
public interface KeywordMapper {
    int insertData(Keyword keyword);

    int deleteById(int id);
    int deleteByProcedureId(String procedureId);

    String selectById(int id);
    List<String> selectByValue(String value);
}
