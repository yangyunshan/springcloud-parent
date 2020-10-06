package com.sensorweb.sossensorservice.service.sos;

import com.alibaba.fastjson.JSONObject;
import com.sensorweb.sossensorservice.dao.*;
import com.sensorweb.sossensorservice.entity.sos.*;
import com.sensorweb.sossensorservice.util.DataCenterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vast.ows.OWSException;
import org.vast.xml.DOMHelperException;
import org.vast.xml.XMLReaderException;

import java.time.Instant;
import java.util.*;

@Service
public class DescribeSensorExpandService {
    @Autowired
    private DescribeSensorService service;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private ProcedureMapper procedureMapper;

    @Autowired
    private ValidTimeMapper validTimeMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private IdentificationMapper identificationMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private VectorMapper vectorMapper;

    @Autowired
    private TextMapper textMapper;

    @Autowired
    private QuantityRangeMapper quantityRangeMapper;

    @Autowired
    private QuantityMapper quantityMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ClassificationMapper classificationMapper;

    @Autowired
    private ComponentMapper componentMapper;

    private static final String PROCEDURE_DESCRIPTION_FORMAT = "http://www.opengis.net/sensorML/2.0";

    /**
     * 已知传感器集合Ids查传感器信息
     * @param procedureIds
     * @return
     * @throws XMLReaderException
     * @throws DOMHelperException
     * @throws OWSException
     */
    public List<Map<String, Object>> getSensorByIds(List<String> procedureIds) {
        Set<String> tempIds;
        List<Map<String, Object>> res = new ArrayList<>();

        if (procedureIds!=null && procedureIds.size()>0) {
            tempIds = new HashSet<>(procedureIds);
        } else {
            return null;
        }
        for (String procedureId : tempIds) {
            Map<String, Object> temp = new HashMap<>();
            Procedure procedure = procedureMapper.selectByIdAndFormat(procedureId, PROCEDURE_DESCRIPTION_FORMAT);

            temp.put("id", procedureId);
            temp.put("name", procedure.getName());
            temp.put("description", procedure.getDescription());
            temp.put("url", procedure.getDescriptionFile());
            temp.put("platformName", StringUtils.isBlank(getPlatformNameOfSensor(procedureId)) ? "":getPlatformNameOfSensor(procedureId));
            temp.put("platformId", StringUtils.isBlank(getPlatformIdOfSensor(procedureId)) ? "":getPlatformIdOfSensor(procedureId));

            res.add(temp);
        }
        return res;
    }

    /**
     * 通过sensorML_Url获取到SensorML内容
     * @param sensorMLPath
     * @return
     */
    public String getSensorContentById(String sensorMLPath) {
        Map<String, Object> res = new HashMap<>();
        if (StringUtils.isBlank(sensorMLPath)) {
            res.put("content", "");
        }
        res.put("content", DataCenterUtils.readFromFile(sensorMLPath));
        return JSONObject.toJSONString(res);
    }

    /**
     * 通过关键字查询传感器
     * @param keyword
     * @return
     */
    public List<String> getSensorByKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        return keywordMapper.selectByValue(keyword);
    }

    /**
     * 通过id查Sensor基本信息
     * @param procedureId
     * @return id
     */
    public List<String> getSensorById(String procedureId) {
        if (StringUtils.isBlank(procedureId)) {
            return null;
        }
        List<String> ids = new ArrayList<>();
        ids.add(procedureId);
        return ids;
    }

    /**
     * 根据时间查询传感器基本信息
     * @param begin
     * @param end
     * @return id
     */
    public List<String> getSensorByValidTime(Instant begin, Instant end) {
        return validTimeMapper.selectByTime(begin, end);
    }

    /**
     * 通过位置名称查询传感器基本信息(模糊查询)
     * @param name
     * @return id
     */
    public List<String> getSensorByPosition(String name) {
        return positionMapper.selectByName(name);
    }

    /**
     * 通过包围盒查询传感器基本信息
     * @param minX
     * @param minY
     * @param maxX
     * @param maxY
     * @return id
     */
    public List<String> getSensorByPosition(double minX, double minY, double maxX, double maxY) {
        return positionMapper.selectByEnvelope(minX, minY, maxX, maxY);
    }

    /**
     * 获取系统内所有的procedureId
     * @return id
     */
    public List<String> getAll() {
        return procedureMapper.selectAll();
    }

    /**
     * 获取系统内所有的传感器Id
     * @return id
     */
    public List<String> getAllSensor() {
        return procedureMapper.selectAllSensorIds();
    }

    /**
     * 获取系统内所有的传感器平台Id
     * @return id
     */
    public List<String> getAllPlatform() {
        return procedureMapper.selectAllPlatformIds();
    }

    /**
     * 获取传感器的平台名称
     * @param procedureId
     * @return name
     */
    public String getPlatformNameOfSensor(String procedureId) {
        Identification identification = identificationMapper.selectByLabelAndProcedureId("所属平台名称", procedureId);
        return identification!=null ? identification.getValue():"";
    }

    /**
     * 获取传感器的平台id
     * @param procedureId
     * @return id
     */
    public String getPlatformIdOfSensor(String procedureId) {
        Identification identification = identificationMapper.selectByLabelAndProcedureId("所属平台标识符", procedureId);
        return identification!=null ? identification.getValue():"";
    }

    /**
     * 通过个体名称查询
     * @param individualName
     * @return id
     */
    public List<String> getSensorByIndividualName(String individualName) {
        return contactMapper.selectByIndividualName(individualName);
    }

    /**
     * 通过组织名称查询
     * @param organizationName
     * @return id
     */
    public List<String> getSensorByOrganizationName(String organizationName) {
        return contactMapper.selectByIndividualName(organizationName);
    }

    /**
     * 通过站点名称查询
     * @param positionName
     * @return id
     */
    public List<String> getSensorByPositionName(String positionName) {
        return contactMapper.selectByIndividualName(positionName);
    }

    /**
     * 获取传感器简称
     * @param procedureId
     * @return name
     */
    public String getShortName(String procedureId) {
        Identification identification = identificationMapper.selectByLabelAndProcedureId("传感器简称", procedureId);
        return identification!=null ? identification.getValue():"";
    }

    /**
     * 通过名称和属性查询传感器
     * @param name
     * @param value
     * @return
     */
    public List<String> getSensorByIdentification(String name, String value) {
        List<String> res = new ArrayList<>();
        List<Identification> identifications = identificationMapper.selectByLabelAndValue(name, value);
        if (identifications.size()>0) {
            for (Identification identification : identifications) {
                res.add(identification.getProcedureId());
            }
        }
        return res;
    }

    /**
     * 获取传感器全称
     * @param procedureId
     * @return name
     */
    public String getLongName(String procedureId) {
        Identification identification = identificationMapper.selectByLabelAndProcedureId("传感器全称", procedureId);
        return identification!=null ? identification.getValue():"";
    }

    /**
     * 通过预期应用名称查询传感器
     * @param applicationName
     * @return id
     */
    public List<String> getSensorByApplication(String applicationName) {
        List<String> res = new ArrayList<>();
        List<Classification> classifications = classificationMapper.selectByLabelAndValue("预期应用", applicationName);
        if (classifications!=null && classifications.size()>0) {
            for (Classification classification : classifications) {
                res.add(classification.getProcedureId());
            }
        }
        return res;
     }

    /**
     * 通过传感器类型名称查询传感器
     * @param className
     * @return id
     */
    public List<String> getSensorByClass(String className) {
        List<String> res = new ArrayList<>();
        List<Classification> classifications = classificationMapper.selectByLabelAndValue("传感器类型", className);
        if (classifications!=null && classifications.size()>0) {
            for (Classification classification : classifications) {
                res.add(classification.getProcedureId());
            }
        }
        return res;
    }

    /**
     * 通过包围盒查询传感器
     * @param lowerCorner
     * @param upperCorner
     * @return
     */
    public List<String> getSensorByEnvelope(double[] lowerCorner, double[] upperCorner) {
        List<String> temp1 = vectorMapper.selectByLowerCorner(lowerCorner[0], lowerCorner[1]);
        List<String> temp2 = vectorMapper.selectByUpperCorner(upperCorner[0], upperCorner[1]);
        //去除重复元素
        temp1.retainAll(temp2);
        List<String> res = new ArrayList<>();
        if (temp1.size()>0) {
            for (String temp : temp1) {
                res.add(DataCenterUtils.removeSuffix(":capability", temp));
            }
        }
        return res;
    }

    /**
     * 通过能力属性查询传感器
     * @param name
     * @param value
     * @return
     */
    public List<String> getSensorByCapability(String name, String value) {
        List<String> temp1 = categoryMapper.selectByNameAndValue(name, value);
        List<String> temp2 = textMapper.selectByNameAndValue(name, value);

        temp1.retainAll(temp2);
        List<String> res = new ArrayList<>();
        if (temp1.size()>0) {
            for (String temp : temp1) {
                res.add(DataCenterUtils.removeSuffix(":capability", temp));
            }
        }
        return res;
    }

    /**
     * 通过能力范围查询传感器
     * @param name
     * @param value
     * @return
     */
    public List<String> getSensorByCapabilityRange(String name, double[] value) {
        List<String> temp1 = quantityMapper.selectByNameAndValue(name, value[0], value[1]);
        List<String> temp2 = quantityRangeMapper.selectByNameAndValue(name, value[0], value[1]);

        temp1.retainAll(temp2);
        //处理id，去掉尾部的内容
        List<String> res = new ArrayList<>();
        if (temp1.size()>0) {
            for (String temp : temp1) {
                res.add(DataCenterUtils.removeSuffix(":capability", temp));
            }
        }
        return res;
    }

    /**
     * 通过特征查询传感器
     * @param name
     * @param value
     * @return
     */
    public List<String> getSensorByCharacteristic(String name, String value) {
        List<String> temp1 = categoryMapper.selectByNameAndValue(name, value);
        List<String> temp2 = textMapper.selectByNameAndValue(name, value);

        temp1.retainAll(temp2);
        List<String> res = new ArrayList<>();
        if (temp1.size()>0) {
            for (String temp : temp1) {
                res.add(DataCenterUtils.removeSuffix(":capability", temp));
            }
        }
        return res;
    }

    /**
     * 判断该id是否已存在
     * @param procedureId
     * @return
     */
    public String isExist(String procedureId) {
        Map<String, String> temp = new HashMap<>();
        temp.put("isExist", procedureMapper.isExist(procedureId) ? "true":"false");
        return JSONObject.toJSONString(temp);
    }

    /**
     * 根据条件查询最终结果
     * @param parameter 封装参数的json格式字符串
     * @return 封装结果的json字符串
     */
    public String searchSensor(String parameter) {
        JSONObject jsonObject = JSONObject.parseObject(parameter);
        List<List<String>> res = new ArrayList<>();
        if (!StringUtils.isBlank(jsonObject.getString("platform"))) {
            res.add(jsonObject.getString("platform").equalsIgnoreCase("true") ? getAllPlatform():getAllSensor());
        }
        if (!StringUtils.isBlank(jsonObject.getString("id"))) {
            res.add(getSensorById(jsonObject.getString("id")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("ShortName"))) {
            res.add(getSensorByIdentification("ShortName", jsonObject.getString("shortName")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("LongName"))) {
            res.add(getSensorByIdentification("LongName", jsonObject.getString("longName")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("intentApplication"))) {
            res.add(getSensorByApplication(jsonObject.getString("intentApplication")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("organizationName"))) {
            res.add(getSensorByOrganizationName(jsonObject.getString("organizationName")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("beginTime")) && !StringUtils.isBlank(jsonObject.getString("endTime"))) {
            res.add(getSensorByValidTime(DataCenterUtils.string2Instant(jsonObject.getString("beginTime")), DataCenterUtils.string2Instant(jsonObject.getString("endTime"))));
        }
        String lowCorner = jsonObject.getString("lowCorner");
        String upperCorner = jsonObject.getString("upperCorner");
        if (!StringUtils.isBlank(lowCorner) && !StringUtils.isBlank(upperCorner)) {
            double[] temp1 = new double[] {Double.parseDouble(lowCorner.split(" ")[0]), Double.parseDouble(lowCorner.split(" ")[1])};
            double[] temp2 = new double[] {Double.parseDouble(upperCorner.split(" ")[0]), Double.parseDouble(upperCorner.split(" ")[1])};
            res.add(getSensorByEnvelope(temp1, temp2));
        }
        if (!StringUtils.isBlank(jsonObject.getString("position"))) {
            res.add(getSensorByPosition(jsonObject.getString("position")));
        }
        if (!StringUtils.isBlank(jsonObject.getString("keyword"))) {
            res.add(getSensorByKeyword(jsonObject.getString("keyword")));
        }


        //remove duplicate element
        List<String> ids = DataCenterUtils.removeDuplicate(res);
        System.out.println(ids.size());
        System.out.println(getSensorByIds(ids));

        return JSONObject.toJSONString(getSensorByIds(ids));
    }

    /**
     * 通过平台查传感器
     * @param platformId
     * @return
     */
    public List<String> getComponentByPlatformId(String platformId) {
        List<String> res = new ArrayList<>();
        List<Component> components = componentMapper.selectByPlatformId(platformId);
        if (components.size()>0) {
            for (Component component : components) {
                res.add(component.getHref());
            }
        }
        return res;
    }

    /**
     * 获取目录树
     * @return
     */
    public String getTOC() {
        List<Map<String, Object>> res = new ArrayList<>();
        List<String> platformIds = getAllPlatform();
        if (platformIds.size()>0) {
            for (String platformId : platformIds) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("platformId", platformId);
                temp.put("platformName", procedureMapper.selectById(platformId).getName());
                temp.put("platformDescription", procedureMapper.selectById(platformId).getDescription());
                List<String> sensorIds = getComponentByPlatformId(platformId);
                temp.put("components", getSensorByIds(sensorIds));
                res.add(temp);
            }
        }
        return JSONObject.toJSONString(res);
    }

    /**
     * 获取传感器信息
     * @return
     */
    public List<Map<String,Object>> getAllSensorInfo() {
        List<Map<String,Object>> res = new ArrayList<>();
        List<String> sensorIds = getAllSensor();
        if (sensorIds.size()>0) {
            for (String sensorId : sensorIds) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("sensorId", sensorId);
                temp.put("name", getLongName(sensorId));
                temp.put("description", procedureMapper.selectById(sensorId).getDescription());
                temp.put("platformId", getPlatformIdOfSensor(sensorId));
                temp.put("platformName", getPlatformNameOfSensor(sensorId));
                res.add(temp);
            }
        }
//        return JSONObject.toJSONString(res);
        return res;
    }

    /**
     * 获取传感器的详细信息
     */
    public Map<String, Object> getDetailInfo(String id) {
        Map<String, Object> res = new HashMap<>();
        if (!StringUtils.isBlank(id)) {

        }
        return res;
    }
}
