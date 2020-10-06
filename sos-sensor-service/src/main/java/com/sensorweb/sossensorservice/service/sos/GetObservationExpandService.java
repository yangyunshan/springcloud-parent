package com.sensorweb.sossensorservice.service.sos;

import com.sensorweb.sossensorservice.dao.ObservationMapper;
import com.sensorweb.sossensorservice.entity.sos.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vast.xml.XMLReaderException;

import java.time.Instant;
import java.util.List;

@Service
public class GetObservationExpandService {
    @Autowired
    private GetObservationService service;

    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private InsertObservationService insertObservationService;

    /**
     * 根据日期查询观测数据
     * @param
     * @param
     * @return
     */
    public List<Observation> getObservationInfo(String procedureId) throws XMLReaderException {
        return observationMapper.selectObservationsBySensorId(procedureId);
    }

    /**
     * 通过日期查询观测数据的条目数
     * @param begin
     * @param end
     * @return
     */
    public int getObservationByDateTime(Instant begin, Instant end) {
        return observationMapper.selectObservationsByDateTime(begin, end);
    }

    public List<Observation> getObservationByProcedureId(String procedureId) {
        return observationMapper.selectObservationsBySensorId(procedureId);
    }

}
