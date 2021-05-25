package com.sensorweb.datacenterairservice.service;

import com.netflix.discovery.converters.Auto;
import com.sensorweb.datacenterairservice.dao.AirQualityHourMapper;
import com.sensorweb.datacenterairservice.dao.AirStationMapper;
import com.sensorweb.datacenterairservice.entity.AirQualityHour;
import com.sensorweb.datacenterairservice.entity.AirStationModel;
import com.sensorweb.datacenterairservice.entity.ChinaAirQualityHour;
import com.sensorweb.datacenterairservice.entity.TWEPA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class GetAirService {

    @Autowired
    private AirQualityHourMapper airQualityHourMapper;

    @Value("${datacenter.path.exportDir}")
    private String exportDir;

    @Autowired
    private AirStationMapper airStationMapper;

    public List<AirQualityHour> getAll() { return airQualityHourMapper.selectAll(); }


    public List<AirQualityHour> getAirQualityHourlyByPage(int pageNum, int pageSize) {
        return airQualityHourMapper.selectByPage(pageNum, pageSize);
    }

    public List<AirQualityHour> getAirQualityHourlyByID(List<String> uniquecode,int pageNum, int pageSize) {
        return airQualityHourMapper.selectByIds(uniquecode,pageNum,pageSize);
    }


    public int getAirQualityHourNum() {
        return airQualityHourMapper.selectNum();
    }

    public int getAirQualityHourlyNumberByID(List<String> unidecode) {
        return airQualityHourMapper.selectNumberByIds(unidecode);
    }

    /**
     * 将湖北省环境监测站的PM数据导出为txt文本文件
     */
    public String exportTXT_WH(List<AirQualityHour> airQualityHours) {
        StringBuilder sb = new StringBuilder();
        //写文件第一行
        sb.append("StationName").append("\t").append("UniqueCode").append("\t").append("longitude").append("\t").append("latitude").append("\t")
                .append("PM25OneHour").append("\r\n");
        if (airQualityHours!=null && airQualityHours.size()>0) {
            for (AirQualityHour airQualityHour:airQualityHours) {
                AirStationModel stationModel = airStationMapper.selectByStationId(airQualityHour.getUniqueCode()).get(0);
                //写文件信息
                sb.append(stationModel.getStationName()).append("\t").append(stationModel.getStationId()).append("\t").
                        append(stationModel.getLon()).append("\t").append(stationModel.getLat()).append("\t")
                        .append(airQualityHour.getPm25OneHour()).append("\r\n");
            }
        }
        return writeTXT(sb.toString());

    }

    /**
     * 将台湾EPA的PM数据导出为txt文本文件
     */
    public String exportTXT_TW(List<TWEPA> twepas) {
        StringBuilder sb = new StringBuilder();
        //写文件第一行
        sb.append("StationName").append("\t").append("UniqueCode").append("\t").append("longitude").append("\t").append("latitude").append("\t")
                .append("PM25OneHour").append("\r\n");
        if (twepas!=null && twepas.size()>0) {
            for (TWEPA twepa:twepas) {
                AirStationModel stationModel = airStationMapper.selectByStationId(twepa.getSiteId()).get(0);
                //写文件信息
                sb.append(stationModel.getStationName()).append("\t").append(stationModel.getStationId()).append("\t").
                        append(stationModel.getLon()).append("\t").append(stationModel.getLat()).append("\t")
                        .append(twepa.getPm25Avg()).append("\r\n");
            }
        }
        return writeTXT(sb.toString());

    }

    /**
     * 将全国的PM数据导出为txt文本文件
     */
    public String exportTXT_CH(List<ChinaAirQualityHour> chinaAirQualityHours) {
        StringBuilder sb = new StringBuilder();
        //写文件第一行
        sb.append("StationName").append("\t").append("UniqueCode").append("\t").append("longitude").append("\t").append("latitude").append("\t")
                .append("PM25OneHour").append("\r\n");
        if (chinaAirQualityHours!=null && chinaAirQualityHours.size()>0) {
            for (ChinaAirQualityHour chinaAirQualityHou:chinaAirQualityHours) {
                AirStationModel stationModel = airStationMapper.selectByStationId(chinaAirQualityHou.getStationCode()).get(0);
                //写文件信息
                sb.append(stationModel.getStationName()).append("\t").append(stationModel.getStationId()).append("\t").
                        append(stationModel.getLon()).append("\t").append(stationModel.getLat()).append("\t")
                        .append(chinaAirQualityHou.getPm2524h()).append("\r\n");
            }
        }
        return writeTXT(sb.toString());

    }

    public String writeTXT(String str) {
        try {
            /* 写入Txt文件 */
            File writename = new File(exportDir + Calendar.getInstance().getTimeInMillis() + "air_data.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(str);
            out.flush();
            out.close();
            return exportDir + Calendar.getInstance().getTimeInMillis() + "air_data.txt";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
