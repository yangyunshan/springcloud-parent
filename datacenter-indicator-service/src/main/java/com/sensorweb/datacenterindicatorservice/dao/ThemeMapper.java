package com.sensorweb.datacenterindicatorservice.dao;

import com.sensorweb.datacenterindicatorservice.entity.Theme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ThemeMapper {
    int insertData(Theme theme);

    List<Theme> selectAll();
    Theme selectByModelId(String modelId);
}
