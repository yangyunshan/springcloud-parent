package com.sensorweb.datacentercwicservice.service;

import com.sensorweb.datacentercwicservice.dao.RecordMapper;
import com.sensorweb.datacentercwicservice.entity.Catalog;
import com.sensorweb.datacentercwicservice.entity.DataSet;
import com.sensorweb.datacentercwicservice.entity.Record;
import com.sensorweb.datacentercwicservice.feign.ObsFeignClient;
import com.sensorweb.datacentercwicservice.feign.SensorFeignClient;
import com.sensorweb.datacentercwicservice.util.CWICConstant;
import com.sensorweb.datacenterutil.utils.DataCenterUtils;
import com.sensorweb.sosobsservice.entity.Observation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InsertCWICService implements CWICConstant {
    private final  String procedureId = "urn:JMA:def:identifier:OGC:2.0:cwic";

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private SensorFeignClient sensorFeignClient;

    @Autowired
    private ObsFeignClient obsFeignClient;

    /**
     * 获取CWIC的GetCapabilities文档
     * @return
     */
    public String getCapabilities() throws IOException {
        String service = "CSW";
        String request = "GetCapabilities";
        String version = "2.0.2";

        String param = "service=" + URLEncoder.encode(service, "utf-8") + "&request=" + URLEncoder.encode(request, "utf-8")
                + "&version=" + URLEncoder.encode(version, "utf-8");
        return DataCenterUtils.doGet(CWICConstant.CWIC_END_POINT, param);
    }

    /**
     * 解析能力文档，获取Catalog数据，其中Catalog里面包含着Dataset数据集信息，有数据集id等字段信息
     * @param str
     * @return
     * @throws DocumentException
     */
    public List<Catalog> getCatalog(String str) throws DocumentException {
        List<Catalog> res = new ArrayList<>();
        Document document = DocumentHelper.parseText(str);
        List<Element> catalogs = document.selectNodes("/csw:Capabilities/ows:OperationsMetadata/ows:ExtendedCapabilities/cwic:ExtendedCapabilities/cwic:FederationMetadata/cwic:catalog");
        for (Element catalog:catalogs) {
            Catalog catalogTemp = new Catalog();
            String catalog_id = catalog.attributeValue("id");
            catalogTemp.setId(catalog_id);
            List<DataSet> dataSets_temp = new ArrayList<>();
            List<Element> dataSets = catalog.selectNodes("cwic:datasets/cwic:dataset");
            for (Element dataset:dataSets) {
                DataSet dataSet = new DataSet();
                String dataset_id = dataset.attributeValue("id");
                String title = dataset.element("title").getText();
                String west = dataset.element("valids").element("EX_GeographicBoundingBox").element("westBoundingLongitude").element("Decimal").getText();
                String east = dataset.element("valids").element("EX_GeographicBoundingBox").element("eastBoundingLongitude").element("Decimal").getText();
                String south = dataset.element("valids").element("EX_GeographicBoundingBox").element("southBoundingLatitude").element("Decimal").getText();
                String north = dataset.element("valids").element("EX_GeographicBoundingBox").element("northBoundingLatitude").element("Decimal").getText();
                String bbox =  "POLYGON((" + west + " " + south + "," + west + " " + north + "," + east + " " + north + ","
                        + east + " " + south + "," + west + " " + south + "))";
                String beginTime = dataset.element("valids").element("EX_TemporalExtent").element("extent").element("TimePeriod").element("beginPosition").getText();
                String endTime = dataset.element("valids").element("EX_TemporalExtent").element("extent").element("TimePeriod").element("endPosition").getText();
                dataSet.setId(dataset_id);
                dataSet.setTitle(title);
                dataSet.setBbox(bbox);
                dataSet.setBegin(beginTime.equals("unknown") ? null:Instant.parse(beginTime));
                dataSet.setEnd(endTime.equals("unknown") ? null:Instant.parse(endTime));
                dataSets_temp.add(dataSet);
            }
            catalogTemp.setDatasets(dataSets_temp);
            res.add(catalogTemp);
        }
        return res;
    }

    /**
     * 调用CWIC目录服务的GetRecords接口,获取响应文档
     * @param datasetId 数据集id,
     * @param spatial_lowerCorner Coordinate under EPSG:4326 conforms to the form: latitude + blank + longitude.
     * @param spatial_upperCorner Coordinate under EPSG:4326 conforms to the form: latitude + blank + longitude.
     * @param temporal_begin “yyyy-MM-ddHH:mm:ss” or “yyyy-MM-ddTHH:mm:ssZ”
     * @param temporal_end “yyyy-MM-ddHH:mm:ss” or “yyyy-MM-ddTHH:mm:ssZ”
     */
    public String getRecordsDoc(String datasetId, String spatial_lowerCorner, String spatial_upperCorner,
                                String temporal_begin, String temporal_end, int startPosition, int maxRecords) throws IOException, ParseException {
        String url = "https://cwic.wgiss.ceos.org/cwicv1/discovery";
        String request = makeRequestOfGetRecords(datasetId, spatial_lowerCorner, spatial_upperCorner, temporal_begin, temporal_end, startPosition, maxRecords);
        return DataCenterUtils.doPost(url, request);
    }

    /**
     * 构造CWIC目录服务GetRecords请求
     */
    public String makeRequestOfGetRecords(String datasetId, String spatial_lowerCorner, String spatial_upperCorner,
                                          String temporal_begin, String temporal_end, int startPosition, int maxRecords) {
        String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<GetRecords \n" +
                "    service=\"CSW\"\n" +
                "    version=\"2.0.2\"\n" +
                "    outputFormat=\"application/xml\"\n" +
                "    outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\"\n" +
                "    resultType=\"results\"\n" +
                "    startPosition=\"" + startPosition + "\"\n" +
                "    maxRecords=\"" + maxRecords + "\"\n" +
                "    xmlns=\"http://www.opengis.net/cat/csw/2.0.2\"\n" +
                "    xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\"\n" +
                "    xmlns:ogc=\"http://www.opengis.net/ogc\"\n" +
                "    xmlns:ows=\"http://www.opengis.net/ows\"\n" +
                "    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" +
                "    xmlns:dct=\"http://purl.org/dc/terms/\"\n" +
                "    xmlns:gml=\"http://www.opengis.net/gml\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2/../../../csw/2.0.2/CSW-discovery.xsd\">\n" +
                "    <Query typeNames=\"csw:Record\">\n" +
                "        <ElementSetName typeNames=\"csw:Record\">full</ElementSetName>\n" +
                "        <Constraint>\n" +
                "            <ogc:Filter>\n" +
                "                <ogc:And>";
        if (StringUtils.isBlank(datasetId)) {
            return "";
        }
        String identifier = "<ogc:PropertyIsEqualTo><ogc:PropertyName>dc:subject</ogc:PropertyName><ogc:Literal>"+ datasetId +"</ogc:Literal></ogc:PropertyIsEqualTo>";
        String spatial = "";
        if (!StringUtils.isBlank(spatial_lowerCorner) && !StringUtils.isBlank(spatial_upperCorner)) {
            spatial = "<ogc:BBOX><ogc:PropertyName>ows:BoundingBox</ogc:PropertyName><gml:Envelope srsName=\"EPSG:4326\"><gml:lowerCorner>"+ spatial_lowerCorner +"</gml:lowerCorner>b<gml:upperCorner>"+ spatial_upperCorner +"</gml:upperCorner>b</gml:Envelope></ogc:BBOX>";
        }
        String temporal = "";
        if (!StringUtils.isBlank(temporal_begin) && !StringUtils.isBlank(temporal_end)) {
            spatial = "<ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyName>TempExtent_begin</ogc:PropertyName><ogc:Literal>"+ temporal_begin +"</ogc:Literal></ogc:PropertyIsGreaterThanOrEqualTo><ogc:PropertyIsLessThanOrEqualTo><ogc:PropertyName>TempExtent_end</ogc:PropertyName><ogc:Literal>"+ temporal_end +"</ogc:Literal></ogc:PropertyIsLessThanOrEqualTo>";
        }
        String suffix = "</ogc:And>\n" +
                "            </ogc:Filter>\n" +
                "        </Constraint>\n" +
                "    </Query>\n" +
                "</GetRecords>";
        return prefix + identifier + spatial + temporal + suffix;
    }

    /**
     * 解析GetRecords响应文档, 获取Record集合
     */
    public List<Record> getRecords(String str) throws DocumentException {
        List<Record> res = new ArrayList<>();

        Document document = DocumentHelper.parseText(str);
        List<Element> records = document.selectNodes("/csw:GetRecordsResponse/csw:SearchResults/csw:Record");
        for (Element record:records) {
            Record recordTemp = new Record();
            String identifier = record.element("identifier").getText();
            recordTemp.setIdentifier(identifier);
            String title = record.element("title").getText();
            recordTemp.setTitle(title);
            String creator = record.element("creator").getText();
            recordTemp.setCreator(creator);
            String publisher = record.element("publisher").getText();
            recordTemp.setPublisher(publisher);
            String contributor = record.element("contributor").getText();
            recordTemp.setContributor(contributor);
            String mediator = record.element("mediator").getText();
            recordTemp.setMediator(mediator);
            String type = record.element("type").getText();
            recordTemp.setType(type);
            String modified = record.element("modified").getText();
            recordTemp.setModified(Instant.parse(handleTimeStr(modified)));
            String[] temporals = record.element("temporal").getText().split("/");
            recordTemp.setBegin(Instant.parse(handleTimeStr(temporals[0])));
            recordTemp.setEnd(Instant.parse(handleTimeStr(temporals[1])));
            List<Element> references = record.elements("references");
            StringBuilder reference = new StringBuilder();
            for (Element temp:references) {
                if (temp.attributeValue("scheme").equals("urn:x-cwic:Download")) {
                    reference.append("Download:").append(record.element("references").getText()).append("||");

                } else if (temp.attributeValue("scheme").equals("urn:x-cwic:Order")) {
                    reference.append("Order:").append(record.element("references").getText()).append("||");
                } else if (temp.attributeValue("scheme").equals("urn:x-cwic:Browse")) {
                    reference.append("Browse:").append(record.element("references").getText()).append("||");
                }
            }
            reference.delete(reference.length()-"||".length(),reference.length());
            recordTemp.setReference(reference.toString());

            String lowerCorner = record.element("WGS84BoundingBox").element("LowerCorner").getText();
            String upperCorner = record.element("WGS84BoundingBox").element("UpperCorner").getText();
            if (!StringUtils.isBlank(lowerCorner) && !StringUtils.isBlank(upperCorner)) {
                String wkt = "POLYGON((" + lowerCorner + "," +
                        lowerCorner.split(" ")[0] + " " + upperCorner.split(" ")[1] + "," +
                        upperCorner + "," +
                        upperCorner.split(" ")[0] + " " + lowerCorner.split(" ")[1] + "," +
                        lowerCorner + "))";
                recordTemp.setWkt(wkt);
            }
            recordTemp.setBbox(lowerCorner + "," + upperCorner);
            res.add(recordTemp);
        }
        return res;
    }

    /**
     * 处理CWIC的时间字符串，转为能够用Instant解析的
     */
    public String handleTimeStr(String str) {
        if (!str.contains("T")) {
            String[] temp = str.split(" ");
            if (temp.length<2 || StringUtils.isBlank(temp[1])) {
                str = temp[0] + "T:00:00:00Z";
            } else {
                str = temp[0] + "T" + temp[1].substring(0, 8) + "Z";
            }
        } else {
            String[] temp = str.split("T");
            if (StringUtils.isBlank(temp[1])) {
                str = temp[0] + "T:00:00:00Z";
            } else {
                str = temp[0] + "T" + temp[1].substring(0, 8) + "Z";
            }
        }
        return str;
    }

    /**
     * 获取record的记录数
     */
    public int getNumOfRecords(String str) throws DocumentException {
        int res = 0;

        Document document = DocumentHelper.parseText(str);
        Element searchResults = (Element) document.selectNodes("/csw:GetRecordsResponse/csw:SearchResults").get(0);
        res = Integer.parseInt(searchResults.attributeValue("numberOfRecordsMatched"));

        return res;
    }

    /**
     * 获取nextRecord,即下一页开始的位置
     */
    public int getNextRecord(String str) throws DocumentException {
        int res = 0;

        Document document = DocumentHelper.parseText(str);
        Element searchResults = (Element) document.selectNodes("/csw:GetRecordsResponse/csw:SearchResults").get(0);
        res = Integer.parseInt(searchResults.attributeValue("nextRecord"));

        return res;
    }

    /**
     * 通过数据集、时空参数接入数据
     */
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertData(String catalog, String datasetId, String spatial_lowerCorner, String spatial_upperCorner,
                        String temporal_begin, String temporal_end, int startPosition, int maxRecords) throws Exception {
        String str = getRecordsDoc(datasetId, spatial_lowerCorner, spatial_upperCorner, temporal_begin, temporal_end, startPosition, maxRecords);
        int count = getNumOfRecords(str);
        while (startPosition<count) {
            String recordDoc = getRecordsDoc(datasetId, spatial_lowerCorner, spatial_upperCorner, temporal_begin, temporal_end, startPosition, maxRecords);
            List<Record> records = getRecords(recordDoc);
            //将record记录插入到数据库中
            if (records!=null && records.size()>0) {
                for (Record record:records) {
                    int status = recordMapper.insertData(record);
                    if (status>0) {
                        Observation observation = new Observation();
                        observation.setProcedureId(procedureId + ":" + catalog);
                        observation.setMapping("cwic_record");
                        observation.setType("file");
                        observation.setObsProperty("");
                        observation.setObsTime(record.getEnd());
                        observation.setBeginTime(record.getBegin());
                        observation.setEndTime(record.getEnd());
                        observation.setBbox(record.getBbox());
                        observation.setWkt(record.getWkt());
                        observation.setName(record.getTitle());
                        observation.setOutId(record.getId());
                        boolean flag = sensorFeignClient.isExist(observation.getProcedureId());
                        if (flag) {
                            obsFeignClient.insertData(observation);
                        } else {
                            log.info("procedure: " + observation.getProcedureId() + "不存在");
                            throw new Exception("procedure: " + observation.getProcedureId() + "不存在");
                        }
                    }
                }
            }
            startPosition = getNextRecord(str);
        }
    }

}
