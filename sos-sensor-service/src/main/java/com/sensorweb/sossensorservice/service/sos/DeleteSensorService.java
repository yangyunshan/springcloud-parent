package com.sensorweb.sossensorservice.service.sos;

import com.sensorweb.sossensorservice.dao.*;
import com.sensorweb.sossensorservice.entity.sos.Component;
import com.sensorweb.sossensorservice.entity.sos.Procedure;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.vast.ows.OWSException;
import org.vast.ows.swe.DeleteSensorReaderV20;
import org.vast.ows.swe.DeleteSensorRequest;
import org.vast.ows.swe.DeleteSensorResponse;
import org.vast.ows.swe.DeleteSensorResponseWriterV20;
import org.vast.xml.DOMHelper;
import org.vast.xml.DOMHelperException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

@Service
public class DeleteSensorService {

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CapabilityMapper capabilityMapper;

    @Autowired
    private CharacteristicMapper characteristicMapper;

    @Autowired
    private ClassificationMapper classificationMapper;

    @Autowired
    private IdentificationMapper identificationMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private TelephoneMapper telephoneMapper;

    @Autowired
    private ValidTimeMapper validTimeMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private VectorMapper vectorMapper;

    @Autowired
    private TextMapper textMapper;

    @Autowired
    private QuantityMapper quantityMapper;

    @Autowired
    private QuantityRangeMapper quantityRangeMapper;

    @Autowired
    private ObservationMapper observationMapper;

    @Autowired
    private FoiMapper foiMapper;

    /**
     * 解析DeleteSensorRequest请求，获取DeleteSensorRequest对象
     * @param requestContent
     * @return
     * @throws DOMHelperException
     * @throws OWSException
     */
    public DeleteSensorRequest getDeleteSensorRequest(String requestContent) throws DOMHelperException, OWSException {
        if (StringUtils.isBlank(requestContent)) {
            return null;
        }

        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(requestContent.getBytes()), false);
        DeleteSensorReaderV20 reader = new DeleteSensorReaderV20();
        return reader.readXMLQuery(domHelper, domHelper.getRootElement());
    }

    /**
     * 解析DeleteSensorRequest请求，获取请求中的procedureId参数
     * @param request
     * @return
     */
    public String getProcedureId(DeleteSensorRequest request) {
        if (request==null) {
            return null;
        }
        return request.getProcedureId();
    }

    /**
     * 根据请求中的待删除的procedureId参数，删除procedure以及相关联的offering
     * @param procedureId
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String deleteSensor(String procedureId) throws DOMHelperException {
        if (StringUtils.isBlank(procedureId)) {
            return null;
        }
        //如果是平台，则在删除平台前需要先删除附属与该平台下的所有传感器
        if (isPlatform(procedureId)) {
            //查询该平台下的所有传感器
            List<Component> components = componentMapper.selectByPlatformId(procedureId);
            if (components.size()>0) {
                for (Component component : components) {
                    String id = component.getHref();
                    deleteSensorById(id);
                }
                componentMapper.deleteByPlatformId(procedureId);
                //删除平台
                deleteSensorById(procedureId);
            }
        } else {
            //首先查询该传感器处于哪个平台
            Component component = componentMapper.selectByHref(procedureId);
            String platformId = component.getPlatformId();
            //更新平台内容
            flushPlatform(procedureId, platformId);
            //删除component表里的相关数据
            componentMapper.deleteByHref(procedureId);
            deleteSensorById(procedureId);
        }
        return procedureId;
    }

    /**
     * 根据id删除procedure，删除时与插入时对应，不能漏删(Component节点留在别处处理)
     * @param id
     */
    public void deleteSensorById(String id) {
        if (!StringUtils.isBlank(id)) {
            //delete identification
            identificationMapper.deleteByProcedureId(id);
            //delete classification
            classificationMapper.deleteByProcedureId(id);
            //delete characteristic
            String characteristicId = id + ":characteristic";
            categoryMapper.deleteByOutId(characteristicId);
            quantityMapper.deleteByOutId(characteristicId);
            quantityRangeMapper.deleteByOutId(characteristicId);
            textMapper.deleteByOutId(characteristicId);
            vectorMapper.deleteByOutId(characteristicId);
            characteristicMapper.deleteByProcedureId(id);
            //delete capability
            String capabilityId = id + ":capability";
            categoryMapper.deleteByOutId(capabilityId);
            quantityMapper.deleteByOutId(capabilityId);
            quantityRangeMapper.deleteByOutId(capabilityId);
            textMapper.deleteByOutId(capabilityId);
            vectorMapper.deleteByOutId(capabilityId);
            capabilityMapper.deleteByProcedureId(id);
            //delete validTime
            validTimeMapper.deleteByProcedureId(id);
            //delete contact
            addressMapper.deleteByProcedureId(id);
            telephoneMapper.deleteByProcedureId(id);
            contactMapper.deleteByProcedureId(id);
            //delete position
            positionMapper.deleteByProcedureId(id);
            //delete keywords
            keywordMapper.deleteByProcedureId(id);
            //delete sensorML File
            Procedure procedure = procedureMapper.selectById(id);
            new File(procedure.getDescriptionFile()).delete();
            //delete observation
            observationMapper.deleteByProcedureId(id);
            foiMapper.deleteByProcedureId(id);
            //delete procedure
            procedureMapper.deleteById(id);
        }
    }

    /**
     * 删除成功，返回相应请求
     * @param procedureId 成功删除的procedure的id
     * @return
     * @throws OWSException
     */
    public Element getDeleteSensorResponse(String procedureId) throws OWSException {
        DeleteSensorResponse response = new DeleteSensorResponse("SOS");
        response.setDeletedProcedure(procedureId);
        DOMHelper domHelper = new DOMHelper();
        DeleteSensorResponseWriterV20 writer = new DeleteSensorResponseWriterV20();
        return writer.buildXMLResponse(domHelper, response, "2.0");
    }

    /**
     * 判断是否为平台还是传感器
     * @param procedureId
     * @return
     */
    public boolean isPlatform(String procedureId) {
        Procedure procedure = procedureMapper.selectById(procedureId);
        return procedure.getIsPlatform()==1;
    }

    /**
     * 更新平台xml中的内容，主要是删除部分component节点
     * @param procedureId :传感器id
     * @param platformId : 平台id
     */
    public void flushPlatform(String procedureId, String platformId) throws DOMHelperException {
        Procedure platform = procedureMapper.selectById(platformId);
        DOMHelper domHelper = new DOMHelper(new ByteArrayInputStream(DataCenterUtils.readFromFile(platform.getDescriptionFile()).getBytes()), false);
        Element element = domHelper.getElement("components/ComponentList");
        NodeList nodeList = element.getElementsByTagName("sml:component");
        int count = nodeList.getLength();
        for (int i = count; i>0; i--) {
            Node node = nodeList.item(i-1);
            String href = ((Element) node).getAttribute("href");
            if (href.equals(procedureId)) {
                element.removeChild(node);
            }
        }
        DataCenterUtils.write2File(platform.getDescriptionFile(), DataCenterUtils.element2String(domHelper.getRootElement()));
    }
}
