package com.sensorweb.datacentergeeservice.controller;

import com.sensorweb.datacentergeeservice.entity.Img;
import com.sensorweb.datacentergeeservice.service.GoogleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
public class GetGeeController {

    @Autowired
    GoogleService imgService;

    @GetMapping(value = "getLandsatByattribute")
    @ResponseBody
    public Map<String, List<Img>> getInfoByattribute(@Param("spacecraftID") String spacecraftID, @Param("Date") String Date, @Param("Cloudcover") String Cloudcover, @Param("imageType") String imageType) {
        Map<String, List<Img>> res = new HashMap<>();
        List<Img> info =  imgService.getByattribute(spacecraftID,Date,Cloudcover,imageType);
        res.put("Info", info);
        return res;

    }


    @GetMapping(value = "getLandsat")
    @ResponseBody
    public Map<String, List<Img>> getAllInfo() {
        Map<String, List<Img>> res = new HashMap<>();
        List<Img> info =  imgService.getAll();
        res.put("Info", info);
        return res;
    }


    @ApiOperation("分页查询数据")
    @GetMapping(path = "getLandsatByPage")
    public Map<String, List<Img>> getLandsatByPage(@ApiParam(name = "pageNum", value = "当前页码") @Param("pageNum") int pageNum,
                                                                     @ApiParam(name = "pageSize", value = "每页的数据条目数") @Param("pageSize") int pageSize) {
        Map<String, List<Img>> res = new HashMap<>();
        List<Img> info =  imgService.getLandsatByPage(pageNum, pageSize);
        res.put("Info", info);
        return res;

    }


}
