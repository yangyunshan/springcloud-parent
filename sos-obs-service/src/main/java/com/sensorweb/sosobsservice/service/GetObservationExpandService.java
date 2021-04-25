package com.sensorweb.sosobsservice.service;

import com.sensorweb.sosobsservice.dao.ObservationMapper;
import com.sensorweb.sosobsservice.entity.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetObservationExpandService {
    @Autowired
    private ObservationMapper observationMapper;

    /**
     * 通过时空条件和类型查询数据
     */
    public List<Observation> getObservationByConditions(String bbox, Instant timeBegin, Instant timeEnd, String type) {
        List<Observation> res = new ArrayList<>();

        List<Observation> tempsByBBox = getObservationByEnvelope(bbox);
        List<Observation> temps = new ArrayList<>();
        for (Observation tempByBBox:tempsByBBox) {
            if (tempByBBox.getBeginTime().isAfter(timeBegin) && tempByBBox.getEndTime().isBefore(timeEnd)) {
                temps.add(tempByBBox);
            }
        }

        for (Observation temp:temps) {
            if (temp.getType().equals(type)) {
                res.add(temp);
            }
        }

        return res;
    }

    /**
     * 查询所有的观测数据
     */
    public int getAllObservationNum() {
        return observationMapper.selectNum();
    }

    /**
     * 分页查询观测数据
     */
    public List<Observation> getObservationByPage(int pageNum, int pageSize) {
        return observationMapper.selectByPage(pageNum, pageSize);
    }

    /**
     * 通过procedureId查询观测数据
     */
    public List<Observation> getObservationsByProcedureId(String procedureId) {
        return observationMapper.selectByProcedureId(procedureId);
    }

    /**
     * 通过日期范围查询观测数据
     */
    public List<Observation> getObservationByTemporal(Instant begin, Instant end) {
        return observationMapper.selectByTemporal(begin, end);
    }

    /**
     * 通过观测类型查询观测数据
     */
    public List<Observation> getObservationByType(String type) {
        return observationMapper.selectByObsType(type);
    }

    /**
     * 通过观测属性查询观测数据
     */
    public List<Observation> getObservationByObsProp(String obsProperty) {
        return observationMapper.selectByObsProp(obsProperty);
    }

    /**
     * 通过观测范围查询观测数据
     * @param bbox wkt格式
     */
    public List<Observation> getObservationByEnvelope(String bbox) {
        return observationMapper.selectBySpatial(bbox);
    }

    /**
     * 通过Observation获取具体观测值
     */
//    @Autowired
//    private AirQualityHourMapper airQualityHourMapper;
//    @Autowired
//    private HimawariMapper himawariMapper;
//    @Autowired
//    private EntryMapper entryMapper;
//    @Autowired
//    private RecordMapper recordMapper;
    public Object getValue(Observation observation) {
//        String table = observation.getMapping();
//        int id = observation.getOutId();
//        switch (table) {
//            case "air_quality_hourly": {
//                return airQualityHourMapper.selectById(id);
//            } case "himawari": {
//                return himawariMapper.selectById(id);
//            } case "entry": {
//                return entryMapper.selectById(id);
//            } case "record": {
//                return recordMapper.selectById(id);
//            } default: {
//                return "";
//            }
//        }
        return "";
    }

    /**
     * 将查询后的Observation转换为O&M模型格式
     */
    public String getOMByObs(Observation observation) {
        String res = "";

//        OMModel omModel = new OMModel();
//        //元数据内容
//        omModel.setIdentifier(observation.getProcedureId());
//        omModel.setName(observation.getName());
//        omModel.setDescription(observation.getDescription());
//        omModel.setObservableProperty(observation.getObsProperty());
//        omModel.setResultTime(observation.getObsTime());
//        Instant[] phenomenon = new Instant[2];
//        phenomenon[0] = observation.getBeginTime();
//        phenomenon[1] = observation.getEndTime();
//        omModel.setPhenomenonTime(phenomenon);
//        omModel.setFeatureOfInterest(observation.getBbox());
        //具体观测数据部分

        return res;
    }

}
