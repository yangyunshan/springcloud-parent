package com.sensorweb.datacenterindicatorservice.dao;

import com.sensorweb.datacenterindicatorservice.entity.SubTheme;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface SubThemeMapper {
    int insertData(SubTheme subTheme);
}
