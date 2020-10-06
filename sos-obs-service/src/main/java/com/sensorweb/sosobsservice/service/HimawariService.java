package com.sensorweb.sosobsservice.service;

import com.sensorweb.sosobsservice.entity.himawari.Aerosol;
import com.sensorweb.sosobsservice.service.InsertObservationService;
import com.sensorweb.sosobsservice.util.DataCenterConstant;
import com.sensorweb.sosobsservice.util.DataCenterUtils;
import com.sensorweb.sosobsservice.util.SWEModelUtils;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataRecord;
import net.opengis.swe.v20.Quantity;
import net.opengis.swe.v20.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vast.data.QuantityImpl;
import org.vast.data.SWEFactory;
import org.vast.data.TextImpl;
import org.vast.ogc.def.DefinitionRef;
import org.vast.ogc.gml.FeatureRef;
import org.vast.ogc.gml.IGeoFeature;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.IProcedure;
import org.vast.ogc.om.ObservationImpl;
import org.vast.ogc.om.ProcedureRef;
import org.vast.ogc.xlink.IXlinkReference;
import org.vast.util.TimeExtent;
import org.vast.xml.XMLWriterException;

import java.io.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class HimawariService implements DataCenterConstant {

    @Value("${datacenter.path.himawari}")
    private String upload;

    @Value("${datacenter.domain}")
    private String baseUrl;

    @Autowired
    private InsertObservationService service;

    private static Logger logger = Logger.getLogger(HimawariService.class);

    /**
     * 每隔一个小时执行一次，为了以小时为单位接入数据
     */
    @Scheduled(cron = "00 32 * * * ?")//每小时的35分00秒执行一次(本来是每小时的30分数据更新一次，但是由于数据量的关系，可能造成在半点的时候数据并没有完成上传而导致的获取数据失败，所以这里提前半个小时，)
    public void insertDataByHour() throws ParseException {
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).minusHours(8);
        System.out.println(dateTime);

        int year = dateTime.getYear();
        Month month = dateTime.getMonth();
        String monthValue = month.getValue()<10?"0"+month.getValue():month.getValue()+"";
        String day = dateTime.getDayOfMonth()<10?"0"+dateTime.getDayOfMonth():dateTime.getDayOfMonth()+"";
        String hour = dateTime.getHour()<10?"0"+dateTime.getHour():dateTime.getHour()+"";
        String minute = dateTime.getMinute()<10?"0"+dateTime.getMinute():dateTime.getMinute()+"";
        Aerosol aerosol = getData(year+"", monthValue+"", day+"", hour+"",minute+"");

        if (aerosol==null) {
            return;
        }
        IObservation observation = getIObservation(aerosol);
        try {
            service.insertObservation(observation);
        } catch (Exception e) {
            String url = observation.getResult().getComponent("url").getData().getStringValue();
            new File(url).delete();
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据，接入2015-07-07T01:00:00至今的数据
     * @throws ParseException
     * @throws XMLWriterException
     */
//    @PostConstruct
    public void insertInitData() throws ParseException, XMLWriterException {
         String dateTime = "2015-07-07T01:00:00";
         insertData(dateTime);
    }

    /**
     * Registry Himawari Data, from dateTime to now
     * @param dateTime YYYY-DD-MMThh:mm:ss  is start time
     */
    public void insertData(String dateTime) throws ParseException, XMLWriterException {
        LocalDateTime time = DataCenterUtils.string2LocalDateTime(dateTime);
        List<Aerosol> aerosols = new ArrayList<>();
        while (time.isBefore(LocalDateTime.now(ZoneId.of("Asia/Shanghai")))) {
            LocalDateTime temp = time.minusHours(8);
            int year = temp.getYear();
            Month month = temp.getMonth();
            String monthValue = month.getValue()<10?"0"+month.getValue():month.getValue()+"";
            String day = temp.getDayOfMonth()<10?"0"+temp.getDayOfMonth():temp.getDayOfMonth()+"";
            String hour = temp.getHour()<10?"0"+temp.getHour():temp.getHour()+"";
            String minute = temp.getMinute()<10?"0"+temp.getMinute():temp.getMinute()+"";
            Aerosol aerosol = getData(year+"", monthValue+"", day+"", hour+"",minute+"");
            aerosols.add(aerosol);
            IObservation observation = getIObservation(aerosol);
            try {
                service.insertObservation(observation);
            } catch (Exception e) {
                String url = observation.getResult().getComponent("url").getData().getStringValue();
                new File(url).delete();
            }
            time = time.plusHours(1);
        }
    }

    /**
     * get id of IObservation object
     * @param aerosol
     * @return
     */
    public String getId(Aerosol aerosol) {
        return aerosol.getName();
    }

    /**
     * get IProcedure of Aerosol object
     * @param aerosol
     * @return
     */
    public IProcedure getProcedure(Aerosol aerosol) {
        IProcedure procedure = new ProcedureRef();
        String id = "urn:JMA:def:identifier:OGC:2.0:Himawari-8-components";
        ((IXlinkReference)procedure).setHref(id);
        return procedure;
    }

    /**
     * get resultTime of Aerosol object
     * @param aerosol
     * @return
     */
    public Instant getRusltTime(Aerosol aerosol) {
        Instant res = aerosol.getTime();
        return res;
    }

    /**
     * get PhenomenonTime of Aerosol object
     * @param aerosol
     * @return
     */
    public TimeExtent getPhenomenonTime(Aerosol aerosol) {
        Instant end = getRusltTime(aerosol);
        Instant begin = end.minusSeconds(60 * 60);
        if (end != null && begin != null) {
            return TimeExtent.period(begin, end);
        } else {
            return null;
        }
    }

    /**
     * get FeatureOfInterest of Aerosol object
     * @param aerosol
     * @return
     */
    public IGeoFeature getFeatureOfInterset(Aerosol aerosol) {
        String name = aerosol.getArea();
        String id = aerosol.getName();

        FeatureRef<?> ref = new FeatureRef<>();
        ref.setHref(name);
        return ref;

//        SamplingFeature<AbstractGeometry> res = new SamplingFeature<>();
//        res.setUniqueIdentifier(id);
//        res.setName(name);
//        return res;
    }

    /**
     * get ObservedProperty of Aerosol object
     * @param aerosol
     * @return
     */
    public DefinitionRef getObservedProperty(Aerosol aerosol) {
        DefinitionRef observedProperty = new DefinitionRef();
        String property = "HimawariARP";
        observedProperty.setHref(property);
        return observedProperty;
    }

    /**
     * get Result of Aerosol object
     * @param aerosol
     * @return
     */
    public DataComponent setDataComponent(Aerosol aerosol) {
        SWEFactory factory = new SWEFactory();
        DataRecord dataRecord = factory.newDataRecord();
        writeResult(aerosol, dataRecord);
        return dataRecord;
    }

    /**
     * get result content of Aerosol object
     * @param aerosol
     * @param dataRecord
     */
    public void writeResult(Aerosol aerosol, DataRecord dataRecord) {
        if (aerosol!=null) {
            Text url = new TextImpl();
            SWEModelUtils.setText(url, aerosol.getUrl(), "url");
            Quantity pixelNum = new QuantityImpl();
            SWEModelUtils.setQuantity(pixelNum, aerosol.getPixelNum()+"", "PixelNumber", "");
            Quantity lineNum = new QuantityImpl();
            SWEModelUtils.setQuantity(lineNum, aerosol.getLineNum()+"", "LineNumber", "");
            dataRecord.addField("url", url);
            dataRecord.addField("pixelNumber", pixelNum);
            dataRecord.addField("lineNumber", lineNum);
        }
    }

    /**
     * get IObservation object by Aerosol object
     * @param aerosol
     * @return
     */
    public IObservation getIObservation(Aerosol aerosol) {
        ObservationImpl observation = new ObservationImpl();
        observation.setId(getId(aerosol));
        observation.setUniqueIdentifier(getId(aerosol));
        observation.setProcedure(getProcedure(aerosol));
        observation.setResultTime(getRusltTime(aerosol));
        observation.setPhenomenonTime(getPhenomenonTime(aerosol));
        observation.setFeatureOfInterest(getFeatureOfInterset(aerosol));
        observation.setPhenomenonTime(getPhenomenonTime(aerosol));
        observation.setObservedProperty(getObservedProperty(aerosol));
        observation.setType("http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement");
        observation.setResult(setDataComponent(aerosol));
        return observation;
    }
    
  /**
     * get the data of APR
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     */
    public Aerosol getData(String year, String month, String day, String hour, String minute) throws ParseException {
        FTPClient ftpClient = getFTPClient(DataCenterConstant.HAMAWARI_HOST, DataCenterConstant.HAMAWARI_USERNAME, DataCenterConstant.HAMAWARI_PASSWORD);
        String fileName = getName(year, month, day, hour, minute);
        String filePath = DataCenterConstant.AREOSOL_PROPERTY_LEVEL3 + year + month + "/" + day + "/";
        String uploadFilePath = upload + "/" + fileName;
        boolean flag = downloadFTP(ftpClient, filePath, fileName, uploadFilePath);

        Aerosol aerosol = new Aerosol();
        if (flag) {
            aerosol.setName(fileName);
            String date = year + "-" + month + "-" + day + "T" + hour + ":00:00";
            Instant temp = DataCenterUtils.string2LocalDateTime(date).plusHours(8).toInstant(ZoneOffset.ofHours(+8));
            aerosol.setTime(temp);
            String url = baseUrl + "/himawari/" + fileName;
            aerosol.setUrl(url);
            return aerosol;
        }
        return null;
    }

    /**
     * get the name of APR by naming convention
     * @param year YYYY
     * @param month MM
     * @param day DD
     * @param hour hh
     * @param minute mm
     * example: H08_20150727_0800_1HARP001_FLDK.02401_02401.nc
     */
    public String getName(String year, String month, String day, String hour, String minute) {
        if (StringUtils.isBlank(year) || StringUtils.isBlank(month) || StringUtils.isBlank(day) || StringUtils.isBlank(hour)) {
            logger.error("parameter is not right");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("H08_").append(year).append(month).append(day).append("_").append(hour).append("00_1HARP030_FLDK.02401_02401.nc");
        return sb.toString();
    }

    /**
     * 获取FTPClient对象
     * @param ftpHost
     * @param userName
     * @param password
     * @return
     */
    public FTPClient getFTPClient(String ftpHost, String userName, String password) {
        FTPClient ftpClient = null;

        try {
            ftpClient = new FTPClient();
            InetAddress inetAddress = InetAddress.getByName(ftpHost);
            ftpClient.connect(inetAddress);
            ftpClient.login(userName, password);
            ftpClient.setConnectTimeout(50000);
            ftpClient.setControlEncoding("UTF-8");

            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.info("未连接到FTP,用户名或密码错误");
                ftpClient.disconnect();
            } else {
                logger.info("FTP连接成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ftpClient;
    }

    /**
     * 关闭FTP方法
     * @param ftpClient
     * @return
     */
    public boolean closeFTP(FTPClient ftpClient) {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            logger.error("FTP关闭失败");
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    logger.error("FTP关闭失败");
                }
            }
        }
        return false;
    }

    /**
     * 下载FTP下指定文件
     * @param ftpClient
     * @param filePath
     * @param fileName
     * @param downPath
     * @return
     */
    public boolean downloadFTP(FTPClient ftpClient, String filePath, String fileName, String downPath) {
        boolean flag = false;
        try {
            //common-net的ftpclient默认是使用ASCII_FILE_TYPE，文件会经过ASCII编码转换，所以可能会造成文件损坏。所以我们需要手动指定其文件类型为二进制文件，屏蔽ASCII转换的操作，避免文件在转换的过程中受损。
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //跳转到文件目录
            ftpClient.changeWorkingDirectory(filePath);
            //获取目录下文件集合
            ftpClient.enterLocalPassiveMode();
            FTPFile[] files = ftpClient.listFiles();
            if(files.length==0) {
//                String str = "H08_20200921_1400_1HARP030_FLDK.02401_02401";
                String year = fileName.substring(4,8);
                String month = fileName.substring(8,10);
                String day = fileName.substring(10,12);
                String hour = fileName.substring(13,15);
                String time = year+"-"+month+"-"+day+"T"+"00:00:00";
                LocalDateTime dateTime = DataCenterUtils.string2LocalDateTime(time);
                dateTime = dateTime.minusDays(1);
                int year1 = dateTime.getYear();
                Month month1 = dateTime.getMonth();
                String monthValue = month1.getValue()<10?"0"+month1.getValue():month1.getValue()+"";
                String day1 = dateTime.getDayOfMonth()<10?"0"+dateTime.getDayOfMonth():dateTime.getDayOfMonth()+"";
                String path = DataCenterConstant.AREOSOL_PROPERTY_LEVEL3 + year1 + monthValue + "/" + day1 + "/";
                ftpClient.changeWorkingDirectory(path);
                files = ftpClient.listFiles();
            }
            FTPFile file = files[files.length-1];
            File downFile = new File(downPath);
            OutputStream out = new FileOutputStream(downFile);
            //绑定输出流下载文件,需要设置编码集,不然可能出现文件为空的情况
            flag = ftpClient.retrieveFile(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1), new FileOutputStream(downFile));
            out.flush();
            out.close();
            if (flag) {
                logger.info("下载成功");
            } else {
                logger.error("下载失败");
            }

//            for (FTPFile file : files) {
//                //取得指定文件并下载
//                if (file.getName().equals(fileName)) {
////                    File downFile = new File(downPath + File.separator + file.getName());
//                    File downFile = new File(downPath);
//                    OutputStream out = new FileOutputStream(downFile);
//                    //绑定输出流下载文件,需要设置编码集,不然可能出现文件为空的情况
//                    flag = ftpClient.retrieveFile(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1), new FileOutputStream(downFile));
//                    out.flush();
//                    out.close();
//                    if (flag) {
//                        logger.info("下载成功");
//                    } else {
//                        logger.error("下载失败");
//                    }
//                }
//            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        closeFTP(ftpClient);
        return flag;
    }

    /**
     * FTP文件上传工具类
     * @param ftp
     * @param filePath
     * @param ftpPath
     * @return
     */
    public boolean uploadFile(FTPClient ftp,String filePath,String ftpPath){
        boolean flag = false;
        InputStream in = null;
        try {
            // 设置PassiveMode传输
            ftp.enterLocalPassiveMode();
            //设置二进制传输，使用BINARY_FILE_TYPE，ASC容易造成文件损坏
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            //判断FPT目标文件夹时候存在不存在则创建
            if(!ftp.changeWorkingDirectory(ftpPath)){
                ftp.makeDirectory(ftpPath);
            }
            //跳转目标目录
            ftp.changeWorkingDirectory(ftpPath);

            //上传文件
            File file = new File(filePath);
            in = new FileInputStream(file);
            String tempName = ftpPath+File.separator+file.getName();
            flag = ftp.storeFile(new String (tempName.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1),in);
            if(flag){
                logger.info("上传成功");
            }else{
                logger.error("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传失败");
        }finally{
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * FPT上文件的复制
     * @param ftp  FTPClient对象
     * @param olePath 原文件地址
     * @param newPath 新保存地址
     * @param fileName 文件名
     * @return
     */
    public boolean copyFile(FTPClient ftp, String olePath, String newPath,String fileName) {
        boolean flag = false;

        try {
            // 跳转到文件目录
            ftp.changeWorkingDirectory(olePath);
            //设置连接模式，不设置会获取为空
            ftp.enterLocalPassiveMode();
            // 获取目录下文件集合
            FTPFile[] files = ftp.listFiles();
            ByteArrayInputStream  in = null;
            ByteArrayOutputStream out = null;
            for (FTPFile file : files) {
                // 取得指定文件并下载
                if (file.getName().equals(fileName)) {

                    //读取文件，使用下载文件的方法把文件写入内存,绑定到out流上
                    out = new ByteArrayOutputStream();
                    ftp.retrieveFile(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1), out);
                    in = new ByteArrayInputStream(out.toByteArray());
                    //创建新目录
                    ftp.makeDirectory(newPath);
                    //文件复制，先读，再写
                    //二进制
                    ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                    flag = ftp.storeFile(newPath+File.separator+(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1)),in);
                    out.flush();
                    out.close();
                    in.close();
                    if(flag){
                        logger.info("转存成功");
                    }else{
                        logger.error("复制失败");
                    }


                }
            }
        } catch (Exception e) {
            logger.error("复制失败");
        }
        return flag;
    }

    /**
     * 实现文件的移动，这里做的是一个文件夹下的所有内容移动到新的文件，
     * 如果要做指定文件移动，加个判断判断文件名
     * 如果不需要移动，只是需要文件重命名，可以使用ftp.rename(oleName,newName)
     * @param ftp
     * @param oldPath
     * @param newPath
     * @return
     */
    public boolean moveFile(FTPClient ftp,String oldPath,String newPath){
        boolean flag = false;

        try {
            ftp.changeWorkingDirectory(oldPath);
            ftp.enterLocalPassiveMode();
            //获取文件数组
            FTPFile[] files = ftp.listFiles();
            //新文件夹不存在则创建
            if(!ftp.changeWorkingDirectory(newPath)){
                ftp.makeDirectory(newPath);
            }
            //回到原有工作目录
            ftp.changeWorkingDirectory(oldPath);
            for (FTPFile file : files) {

                //转存目录
                flag = ftp.rename(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1), newPath+File.separator+new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
                if(flag){
                    logger.info(file.getName()+"移动成功");
                }else{
                    logger.error(file.getName()+"移动失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("移动文件失败");
        }
        return flag;
    }

    /**
     * 删除FTP上指定文件夹下文件及其子文件方法，添加了对中文目录的支持
     * @param ftp FTPClient对象
     * @param FtpFolder 需要删除的文件夹
     * @return
     */
    public boolean deleteByFolder(FTPClient ftp,String FtpFolder){
        boolean flag = false;
        try {
            ftp.changeWorkingDirectory(new String(FtpFolder.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
            ftp.enterLocalPassiveMode();
            FTPFile[] files = ftp.listFiles();
            for (FTPFile file : files) {
                //判断为文件则删除
                if(file.isFile()){
                    ftp.deleteFile(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
                }
                //判断是文件夹
                if(file.isDirectory()){
                    String childPath = FtpFolder + File.separator+file.getName();
                    //递归删除子文件夹
                    deleteByFolder(ftp,childPath);
                }
            }
            //循环完成后删除文件夹
            flag = ftp.removeDirectory(new String(FtpFolder.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
            if(flag){
                logger.info(FtpFolder+"文件夹删除成功");
            }else{
                logger.error(FtpFolder+"文件夹删除成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除失败");
        }
        return flag;

    }

    /**
     * 遍历解析文件夹下所有文件
     * @param folderPath 需要解析的的文件夹
     * @param ftp FTPClient对象
     * @return
     */
    public boolean readFileByFolder(FTPClient ftp,String folderPath){
        boolean flage = false;
        try {
            ftp.changeWorkingDirectory(new String(folderPath.getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
            //设置FTP连接模式
            ftp.enterLocalPassiveMode();
            //获取指定目录下文件文件对象集合
            FTPFile files[] = ftp.listFiles();
            InputStream in = null;
            BufferedReader reader = null;
            for (FTPFile file : files) {
                //判断为txt文件则解析
                if(file.isFile()){
                    String fileName = file.getName();
                    if(fileName.endsWith(".txt")){
                        in = ftp.retrieveFileStream(new String(file.getName().getBytes(StandardCharsets.UTF_8),StandardCharsets.ISO_8859_1));
                        reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                        String temp;
                        StringBuffer buffer = new StringBuffer();
                        while((temp = reader.readLine())!=null){
                            buffer.append(temp);
                        }
                        if(reader!=null){
                            reader.close();
                        }
                        if(in!=null){
                            in.close();
                        }
                        //ftp.retrieveFileStream使用了流，需要释放一下，不然会返回空指针
                        ftp.completePendingCommand();
                        //这里就把一个txt文件完整解析成了个字符串，就可以调用实际需要操作的方法
                        System.out.println(buffer.toString());
                    }
                }
                //判断为文件夹，递归
                if(file.isDirectory()){
                    String path = folderPath+File.separator+file.getName();
                    readFileByFolder(ftp, path);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件解析失败");
        }

        return flage;

    }
}
