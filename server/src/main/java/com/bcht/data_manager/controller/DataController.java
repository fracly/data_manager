package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.*;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.DownloadType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.service.UserService;
import com.bcht.data_manager.utils.*;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 数据Controller
 * 对应数据的CRUD、下载等操作，以及花式查询，数据分为3类
 *      1、Hive表，对应表的增删改查，不加载数据。
 *      2、HBase表，添加对应的HBase表
 *      3、HDFS文件，要上传相应数据。
 *
 *  @author fracly
 *  @date 2020-05-12 17:00:00
 */
@RestController
@RequestMapping("/api/data")
public class DataController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private DataSourceService dataSourceService;

    private DatagramSocket datagramSocket = null;

    /**
     * 数据新增
     *  1、创建HIVE表
     *  2、创建HBase表
     *  3、创建HDFS目录
     *  以上三种类型数据库的创建均可以通过dataService方法里面的create****Data进行创建
     *  创建的时候根据不同数据类型，携带不同形参
     *  如果创建的数据不隶属于以上三种类型数据，那么就提示错误：未知类型
     */
    @PostMapping("/create")
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer createMethod, Integer type, Long dataSourceId, String name,
                         String columns, String tableName, String createSql, String description, String labels, String fileName, Integer status, Integer zzPublic) {
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(dataSourceId);

        // 数据的创建根据数据源的不同，创建方式也不同
        if (type == DbType.HIVE.getIndex()) {
            return dataService.createHiveData(dataSource, loginUser, createMethod, createSql, tableName, columns, name, description, labels, status, zzPublic);
        } else if (type == DbType.HBASE.getIndex()) {
            return dataService.createHBaseData(dataSource, loginUser, tableName, name, description, labels, status, zzPublic);
        } else if (type == DbType.HDFS.getIndex()) {
            return dataService.createHDFSData(dataSource, loginUser, fileName, name, description, labels, status, zzPublic);
        }
        putMsg(result, Status.UNKOWN_DATASOURCE_TYPE);
        return result;
    }

    /**
     * 数据删除
     *
     */
    @GetMapping("/delete")
    public Result delete(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, int id){
        Result result = new Result();
        Data data = dataService.queryById(id); //根据id查找对应数据，并用Data类的data来存储数据对象
        if(data.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) { //如果data对象的创建者ID不等于当前登陆用户的ID或者当前登录用户姓名不等于管理员的姓名
            putMsg(result, Status.CUSTOM_FAILED, "请不要删除他人的数据哦~");
            return result;
        }
        DataSource dataSource = dataService.queryDataSourceByDataId(id);
        if(data.getType() == DbType.HIVE.getIndex()) {
            try{
                HiveUtils.dropTable(dataSource, data.getDataName());
            } catch (Exception e) {
                putMsg(result, Status.HIVE_DROP_TABLE_FAILED);
                logger.error("删除Hive表失败\n" + e.getMessage());
                return result;
            }
        } else if(data.getType() == DbType.HBASE.getIndex()) {
            try{
                HBaseUtils.dropTable(dataSource, data.getDataName());
            } catch (IOException e) {
                putMsg(result, Status.HBASE_DROP_TABLE_FAILED);
                logger.error("删除HBase表失败\n" + e.getMessage());
                return result;
            }
        } else if (data.getType() == DbType.HDFS.getIndex()) {
            try{
                HDFSUtils.deleteHDFSFile(dataSource, data.getDataName());
            } catch (IOException e) {
                putMsg(result, Status.HDFS_DELETE_FILE_FAILED);
                logger.error("删除HDFS文件失败\n" + e.getMessage());
                return result;
            }
        }
        dataService.deleteById(id);
        dataService.deleteDataSourceDataRelation(id);
        dataService.deleteLabelDataRelation(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除数据成功");
        return result;
    }

    /**
     * 数据修改
     */
    @PostMapping("/update")
    public Result update(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        Data data = dataService.queryById(MapUtils.getInt(parameter, "id"));
        if(data.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) {
            putMsg(result, Status.CUSTOM_FAILED, "请不要修改他人的数据哦~");
            return result;
        }
        data.setName(MapUtils.getString(parameter, "name"));
        String labels = MapUtils.getString(parameter, "labels");
        Integer status = MapUtils.getInt(parameter, "status");
        Integer zzPublic = MapUtils.getInt(parameter, "zzPublic");
        String[] ids = labels.replace("[", "").replace("]", "").split(",");
        List<Label> labelList = labelService.queryByDataId(data.getId());
        for(Label label : labelList) {
            labelService.deleteDataLabelRelation(label.getId(), data.getId());
        }
        for(String id: ids) {
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(id)){
                labelService.insertDataLabelRelation(Integer.parseInt(id), data.getId());
            }
        }
        data.setDescription(MapUtils.getString(parameter, "description"));
        data.setUpdateTime(new Date());
        data.setStatus(status);
        data.setZzPublic(zzPublic);
        dataService.update(data);
        putMsg(result, Status.CUSTOM_SUCESSS, "更新数据成功");
        return result;
    }

    /**
     * 数据修改-Hive表增加列
     */
    @PostMapping("/add-column")
    public Result addColumn(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();
        int dataId = MapUtils.getInt(parameter, "dataId");
        Data data = dataService.queryById(dataId);
        if(data.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) {
            putMsg(result, Status.CUSTOM_FAILED, "请不要修改他人的数据哦~");
            return result;
        }

        String columns = MapUtils.getString(parameter, "columns");
        return dataService.addColumn(dataId, columns);
    }

    /**
     * 数据修改-Hive表增加列
     */
    @PostMapping("/modify-column")
    public Result modifyColumn(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        int dataId = MapUtils.getInt(parameter, "dataId");
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        if(data.getCreatorId() != loginUser.getId() && !loginUser.getUsername().equals(Constants.ADMIN)) {
            putMsg(result, Status.CUSTOM_FAILED, "请不要修改他人的数据哦~");
            return result;
        }

        String type = MapUtils.getString(parameter, "type");
        String oldName = MapUtils.getString(parameter, "key");
        String newName = MapUtils.getString(parameter, "name");
        String comment = MapUtils.getString(parameter, "comment");


        return dataService.modifyColumn(dataId, type, oldName, newName, comment);
    }

    /**
     * 数据加密
     */
    @GetMapping("/encrypt")
    public Result encrypt(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @Param("dataId") Long dataId) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        try {
            String abbrFileName = FileUtils.getFileName(data.getDataName());
            String localPath =  FileUtils.getDownloadFilename(abbrFileName);
            HDFSUtils.copyHdfsToLocal(data.getDataName(), localPath, true, true);
            String encryptPath = FileUtils.getUploadFilename(abbrFileName);
            FileUtils.encryptAndDecryptFile(localPath, encryptPath);
            HDFSUtils.copyLocalToHdfs(encryptPath, data.getDataName(), true, true);
        } catch (IOException e) {
            logger.error("数据加密失败" + e.getMessage());
            e.printStackTrace();
            putMsg(result, Status.CUSTOM_FAILED, "数据加密失败");
            return result;
        }

        int count = dataService.encryptData(dataId);
        if(count > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @GetMapping("/decrypt")
    public Result decrypt(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @Param("dataId") Long dataId) {
        Result result = new Result();

        Data data = dataService.queryById(dataId);
        try {
            String abbrFileName = FileUtils.getFileName(data.getDataName());
            String localPath =  FileUtils.getDownloadFilename(abbrFileName);
            HDFSUtils.copyHdfsToLocal(data.getDataName(), localPath, true, true);
            String encryptPath = FileUtils.getUploadFilename(abbrFileName);
            FileUtils.encryptAndDecryptFile(localPath, encryptPath);
            HDFSUtils.copyLocalToHdfs(encryptPath, data.getDataName(), true, true);
        } catch (IOException e) {
            logger.error("数据解密失败" + e.getMessage());
            e.printStackTrace();
            putMsg(result, Status.CUSTOM_FAILED, "数据解密失败");
            return result;
        }

        int count = dataService.decryptData(dataId);
        if(count > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @GetMapping("/batch-decrypt")
    public Result batchDecrypt(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        int count = dataService.batchDecryptData();
        if(count > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @GetMapping("/batch-encrypt")
    public Result batchEncrypt(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        int count = dataService.batchEncryptData();
        if(count > 0) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * 数据查询-根据ID查询
     */
    @GetMapping("/queryById")
    public Result queryById(int id){
        Result result = new Result();
        Data data = dataService.queryById(id);
        result.setData(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据查询-复杂查询，可根据标签、时间、关键字来查询
     */
    @GetMapping("/search")
    public Result search(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int type, String labels, int pageNo, int pageSize, String startTime, String endTime) {
        Result result = new Result();
        name = name.replace("_", "\\_");
        List<Data> targetDataList = null;
        int total;
        if(loginUser.getUsername().equals(Constants.ADMIN)) {
            targetDataList = dataService.search(0, name, type, labels, pageNo, pageSize, startTime, endTime);
            total = dataService.searchTotal(0, name, type, labels, startTime, endTime);
        } else {
            targetDataList = dataService.search(loginUser.getId(), name, type, labels, pageNo, pageSize, startTime, endTime);
            total = dataService.searchTotal(loginUser.getId(), name, type, labels, startTime, endTime);
        }
        for(Data data : targetDataList) {
            List<Label> labelList = labelService.queryByDataId(data.getId());
            data.setLabelList(labelList);
        }
        result.setData(targetDataList);
        Map resultMap = new HashMap();
        resultMap.put("total", total);
        resultMap.put("pageSize", pageSize);
        result.setDataMap(resultMap);
        putMsg(result, Status.SUCCESS);

        // 记录用户的搜索行为
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(name)){
            int count = dataService.logSearch(loginUser.getId(), name);
            if( count == 0) {
                logger.error("记录查询日志失败！");
            }
        }
        return result;
    }

    /**
     * 数据加密-获取数据列表
     * @param dataSourceId
     * @param searchVal
     * @return
     */
    @GetMapping("/encrypt-search")
    public Result encryptSearch(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataSourceId, String searchVal, int pageNo, int pageSize) {
        Result result = new Result();

        List<Data> resultList = dataService.encryptSearch(dataSourceId, searchVal, pageNo, pageSize);
        int total = dataService.encryptSearchTotal(dataSourceId, searchVal);

        result.setData(resultList);
        Map resultMap = new HashMap();
        resultMap.put("total", total);
        result.setDataMap(resultMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/lineage")
    public Result lineage(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataId) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        User user = userService.queryById2(data.getCreatorId());

        Map<String, Object> tmpMap1 = new HashMap<>();
        Map<String, Object> tmpMap2 = new HashMap<>();

        Map<String, Object> map = dataService.lineage(dataId);
        List<Map> keyList = new ArrayList<>();
        List<Map> relationList = new ArrayList<>();
        tmpMap1.put("from", user.getName());
        tmpMap1.put("to", data.getDataName());
        relationList.add(tmpMap1);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            tmpMap1 = new HashMap<>();
            tmpMap1.put("from", data.getDataName());
            tmpMap1.put("to", entry.getKey() + "(" + entry.getValue() + ")");
            relationList.add(tmpMap1);
        }

        tmpMap2.put("key", user.getName());
        tmpMap2.put("color", "lightblue");
        keyList.add(tmpMap2);
        tmpMap2 = new HashMap<>();
        tmpMap2.put("key", data.getDataName());
        tmpMap2.put("color", "orange");
        keyList.add(tmpMap2);
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            tmpMap2 = new HashMap<>();
            tmpMap2.put("key", entry.getKey() + "(" + entry.getValue() + ")");
            tmpMap2.put("color", "pink");
            keyList.add(tmpMap2);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("keyList", keyList);
        resultMap.put("relationList", relationList);
        result.setData(resultMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据元信息查询
     */
    @GetMapping("/detail")
    public Result detail(int dataId) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        if (data.getType() == DbType.HIVE.getIndex()) {
            return dataService.hiveDetail(dataId);
        } else if (data.getType() == DbType.HBASE.getIndex()) {
            return dataService.hbaseDetail(dataId);
        } else if (data.getType() == DbType.HDFS.getIndex()) {
            return dataService.hdfsDetail(dataId);
        }
        putMsg(result, Status.UNKOWN_DATASOURCE_TYPE);
        return result;
    }

    /**
     * 数据查询-预览前1000行数据
     */
    @GetMapping("/preview")
    public Result preview(int dataId, Integer pageNo, Integer pageSize) {
        Data data = dataService.queryById(dataId);
        if(data.getType() == DbType.HDFS.getIndex()) {
            return dataService.hdfsPreview(dataId);
        } else {
            return dataService.tablePreview(dataId);
        }
    }

    /**
     * 发送任务初始化
     * @return
     */
    @GetMapping("/init-send")
    public Result initDataSend() {
        Result result = new Result();
        if( datagramSocket == null || datagramSocket.isClosed()) {
            putMsg(result, Status.SUCCESS);
        }else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    /**
     * 数据发送请求
     */
    @GetMapping("/send")
    public Result send(int dataId, Long minStamp, Long maxStamp, String ip, Integer port , Integer loop) {
        Result result  = new Result();
        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
        String tableName = dataSource.getCategory1() + ":" + data.getDataName();
        List<UDPPacket> packets = null;

        try {
            packets = HBaseUtils.queryUDPPacket(dataSource, tableName, minStamp, maxStamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (packets != null && loop == 1 ) {        //需要循环发送
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
            try {
                datagramSocket = new DatagramSocket();
                while( true ) {
                    for (UDPPacket udpPacket : packets) {
                        byte[] byteArray = udpPacket.getData();
                        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, inetSocketAddress);
                        datagramSocket.send(packet);
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            putMsg(result, Status.CUSTOM_SUCESSS, "UDP数据循环发送成功");
            return result;
        }
        else if(packets != null && loop == 2) {   //不需要循环发送
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);

            try{
                datagramSocket = new DatagramSocket();
                    for(UDPPacket udpPacket : packets){
                        byte[] byteArray = udpPacket.getData();
                        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, inetSocketAddress);
                        datagramSocket.send(packet);
                    }
            } catch (SocketException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                datagramSocket.close();
            }
            putMsg(result, Status.CUSTOM_SUCESSS, "UDP数据单次发送成功");
            return result;
        }
        else {
            putMsg(result, Status.CUSTOM_FAILED, "对应的数据没有任何记录");
            return result;
        }
    }

    /**
     * 停止数据发送请求
     */
    @GetMapping("/stop-send")
    public Result stopSend() {
        Result result = new Result();
        datagramSocket.close();
        putMsg(result, Status.CUSTOM_SUCESSS, "停止UDP发送成功");
        return result;
    }

    /**
     * 数据发送前的统计
     */
    @GetMapping("/send/init")
    public Result sendInit(Integer dataId, Long minStamp, Long maxStamp) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
        String tableName = dataSource.getCategory1() + ":" + data.getDataName();
        Map map = null;
        try {
            map = HBaseUtils.getTableTimestampRange(dataSource, tableName);
            if(map!= null) {
                result.setData(map);
                putMsg(result, Status.SUCCESS);
                return result;
            } else {
                putMsg(result, Status.FAILED);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        putMsg(result, Status.FAILED);
        return result;
    }

    /**
     * 数据查询-根据数据源查询
     */
    @GetMapping("/listByDataSource")
    public Result listByDataSource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataSourceId, Integer pageNo, Integer pageSize, String searchVal) {
        Result result = new Result();
        List<Data> dataList = null;
        int total;
        if(pageNo == null || pageNo == 0) { pageNo = 1; }
        if(pageSize == null || pageSize == 0) { pageSize = 10; }
        if(dataSourceId == null) { dataSourceId = 0; }
        if (loginUser.getUsername().equals(Constants.ADMIN)) {
            dataList = dataService.list(0, dataSourceId, pageNo, pageSize, searchVal);
            total = dataService.listTotal(0, dataSourceId);
        } else {
            dataList = dataService.list(loginUser.getId(), dataSourceId, pageNo, pageSize, searchVal);
            total = dataService.listTotal(loginUser.getId(), dataSourceId);
        }
        for(Data data : dataList) {
            List<Label> labelList = labelService.queryByDataId(data.getId());
            data.setLabelList(labelList);
            DataSource dataSource = dataService.queryDataSourceByDataId(data.getId());
            data.setDataSourceId(dataSource.getId());
            data.setDataSourceName(dataSource.getName());
        }
        result.setData(dataList);
        Map<String, Object> map = new HashMap<>();

        map.put("pageNo", pageNo);
        map.put("total", total);
        result.setDataMap(map);

        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据查询-下载数据
     */
    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity download(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataId, String condition, Integer downloadType) {
        // 根据文件类型判断处理逻辑：
        //  HBase/Hive数据，下载成cvs文件，返回生成 ResponseEntity
        Data data = dataService.queryById(dataId);

        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
        List<String> lineList = null;
        if(data.getType() == DbType.HIVE.getIndex()) {
            if(downloadType == DownloadType.CSV.getIndex()) //如果下载CSV格式
            {
                try{
                    lineList = HiveUtils.downloadTableData(dataSource, data.getDataName(), condition, 0, ",");
                } catch (Exception e) {
                    logger.error("下载Hive表数据失败\n" + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取Hive表数据失败");
                }
            }
            if(downloadType == DownloadType.TXT.getIndex()){ //如果下载TXT格式
                try{
                    lineList = HiveUtils.downloadTableData(dataSource, data.getDataName(), condition, 0, "  ");
                } catch (Exception e) {
                    logger.error("下载Hive表数据失败\n" + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取Hive表数据失败");
                }
            }
            }
        else if(data.getType() == DbType.HBASE.getIndex()){
            try{
                lineList = HBaseUtils.downloadTableData(dataSource, data.getDataName(), Constants.maxDownloadRecord);
            } catch (IOException e) {
                logger.error("下载HBase数据失败\n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取HBase数据失败");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件类型错误！");
        }

        String localFileName = null;
        localFileName = FileUtils.getDownloadFilename(data.getName()) + "." + DownloadType.valueOf(downloadType).toString().toLowerCase();
        File localFile =  new File(localFileName);
        if(!localFile.exists()) {
            localFile.getParentFile().mkdirs();
        }
        try{
            FileWriter fw = new FileWriter(localFileName);
            for(int i = 0; i < lineList.size(); i ++) {
                fw.write(lineList.get(i));
            }
            fw.flush();
            fw.close();
        }catch (IOException e) {
            logger.error("查询数据写入本地文件失败" + e.getMessage());
            e.printStackTrace();
        }
        logDownload(loginUser, (int) data.getId());
        try {
            org.springframework.core.io.Resource file = FileUtils.file2Resource(localFileName);
            if(file == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据文件下载失败");
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(file.getFilename(),"UTF-8") + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件下载出错");
        }
    }

    @GetMapping("/download/url")
    public String downloadUrl(String fileName) {
        String result = "";
        String urlName = PropertyUtils.getString("http.defaultURL") + fileName + "?op=OPEN";
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(urlName).openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(5000);
            String location = conn.getHeaderField("Location");
            int secondIndex = location.indexOf(':', 7);
            String domain = location.substring(7, secondIndex);
            String ip = PropertyUtils.getString(domain);
            return location.replace(domain, ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 数据下载记录接口
     */
    @GetMapping("/download/log")
    public Result logDownload(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataId) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);

        int count = dataService.logDownload(loginUser.getId(), data);
        if(count >0 ) {
            putMsg(result, Status.SUCCESS);
        } else {
            putMsg(result, Status.FAILED);
        }
        return result;
    }
}
