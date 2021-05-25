package com.sensorweb.datacenterweatherservice.service;

import com.sensorweb.datacenterweatherservice.dao.WeatherStationMapper;
import com.sensorweb.datacenterweatherservice.entity.ChinaWeather;
import com.sensorweb.datacenterweatherservice.entity.WeatherStationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
public class GetWeatherInfo {

    @Autowired
    private WeatherStationMapper weatherStationMapper;

    @Value("${datacenter.path.exportDir}")
    private String exportDir;

    /**
     * 将天气信息导出为.txt文本文件,格式指定
     */
    public String exportTXT(List<ChinaWeather> chinaWeathers) {
        StringBuilder sb = new StringBuilder();
        //写文件第一行
        sb.append("Station_id_C").append("\t").append("longitude").append("\t").append("latitude").append("\t")
                .append("WIN_S_AVG_2mi").append("\t").append("TEM").append("\t").append("RHU").append("\t").append("PRS").append("\r\n");
        if (chinaWeathers!=null && chinaWeathers.size()>0) {
            for (ChinaWeather chinaWeather:chinaWeathers) {
                WeatherStationModel stationModel = weatherStationMapper.selectByStationId(chinaWeather.getStationId());
                //写文件信息
                sb.append(stationModel.getStationId()).append("\t").append(stationModel.getLon()).append("\t")
                        .append(stationModel.getLat()).append("\t").append(chinaWeather.getWindP()).append("\t")
                        .append(chinaWeather.getTemperature()).append("\t").append(chinaWeather.getHumidity()).append("\t")
                        .append(chinaWeather.getPressure()).append("\r\n");
            }
        }
        return writeTXT(sb.toString());

    }

    public String writeTXT(String str) {
        try {
            /* 写入Txt文件 */
            File writename = new File(exportDir + Calendar.getInstance().getTimeInMillis() + "weatherdata.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(str);
            out.flush();
            out.close();
            return exportDir + Calendar.getInstance().getTimeInMillis() + "weatherdata.txt";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
