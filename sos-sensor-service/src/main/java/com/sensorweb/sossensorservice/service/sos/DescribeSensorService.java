package com.sensorweb.sossensorservice.service.sos;

import com.sensorweb.sossensorservice.dao.ProcedureMapper;
import com.sensorweb.sossensorservice.entity.sos.Procedure;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import net.opengis.sensorml.v20.AbstractProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vast.ows.OWSException;
import org.vast.ows.swe.DescribeSensorReaderV20;
import org.vast.ows.swe.DescribeSensorRequest;
import org.vast.ows.swe.DescribeSensorResponse;
import org.vast.ows.swe.DescribeSensorResponseWriterV20;
import org.vast.sensorML.SMLUtils;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;

@Service
public class DescribeSensorService {

    @Autowired
    private ProcedureMapper procedureMapper;

    /**
     * 解析DescribeSensor请求，返回请求对象
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public DescribeSensorRequest getDescribeSensorRequest(String requestContent) throws DOMHelperException, OWSException {
        DescribeSensorRequest describeSensorRequest = new DescribeSensorRequest();

        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()),false);
        DescribeSensorReaderV20 describeSensorReader = new DescribeSensorReaderV20();
        describeSensorRequest = describeSensorReader.readXMLQuery(domHelper, domHelper.getRootElement());
        return describeSensorRequest;
    }

    /**
     * 解析DescribeSensorRequest，获取procedureId参数
     * @param request
     * @return
     */
    public String getProcedureId(DescribeSensorRequest request) {
        return request.getProcedureID();
    }

    /**
     * 解析DescribeSensorRequest，获取procedureDescriptionFormat参数
     * @param request
     * @return
     */
    public String getDescriptionFormat(DescribeSensorRequest request) {
        return request.getFormat();
    }

    /**
     * 根据查询返回的Procedure生成DescribeSensorResponse文档，以字符串格式返回
     * @param
     * @return
     * @throws OWSException
     * @throws FileNotFoundException
     * @throws XMLReaderException
     */
    public String getDescribeSensorResponse(String procedureId, String descriptionFormat) throws OWSException, XMLReaderException, DOMHelperException {
        String result = null;
        if (!StringUtils.isBlank(procedureId)) {
            Procedure procedure = procedureMapper.selectByIdAndFormat(procedureId, descriptionFormat);
            if (procedure!=null) {
                DescribeSensorResponse response = new DescribeSensorResponse();
                DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(DataCenterUtils.readFromFile(procedure.getDescriptionFile()).getBytes()),false);

                //获取sensorml的AbstractProcess对象
                SMLUtils smlUtils = new SMLUtils(SMLUtils.SENSORML);
                AbstractProcess abstractProcess = smlUtils.readProcess(domHelper, domHelper.getRootElement());
                response.setProcedureDescription(abstractProcess);
                response.setProcedureDescriptionFormat(procedure.getDescriptionFormat());

                //生成Response对象，并且写入Element
                DescribeSensorResponseWriterV20 responseWriter = new DescribeSensorResponseWriterV20();
                DOMHelper domHelper1 = new DOMHelper();
                Element element = responseWriter.buildXMLResponse(domHelper1, response, "2.0");

                //Element转字符串，返回
                result = DataCenterUtils.element2String(element);
            }
        }

        return result;
    }
}
