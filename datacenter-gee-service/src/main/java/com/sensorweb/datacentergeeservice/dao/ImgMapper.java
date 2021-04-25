package com.sensorweb.datacentergeeservice.dao;

import com.sensorweb.datacentergeeservice.entity.Img;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ImgMapper {


    List<Img> selectByattribute(@Param("spacecraftID") String spacecraftID, @Param("Date") String Date, @Param("Cloudcover") String Cloudcover, @Param("imageType") String imageType);

    List<Img> selectAll();

    List<Img> selectByPage(int pageNum, int pageSize);
}