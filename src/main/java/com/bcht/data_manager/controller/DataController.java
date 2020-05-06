package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Label;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/data")
public class DataController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private DataSourceService dataSourceService;

    @PostMapping("/create")
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer createMethod, Integer type, Long dataSourceId, String name,
                         String columns, String tableName, String createSql, String description, String labels, MultipartFile file) {
        Result result = new Result();
//        int method = MapUtils.getInt(parameter, "createMethod");
//        int type = MapUtils.getInt(parameter, "type");
//        int dataSourceId = MapUtils.getInt(parameter, "dataSourceId");
//
//        String name = MapUtils.getString(parameter, "name");
//        String columns = MapUtils.getString(parameter, "columnList");
//        String tableName = MapUtils.getString(parameter, "tableName");
//        String createSql = MapUtils.getString(parameter, "createSql");
//        String description = MapUtils.getString(parameter, "description");
//        String labels = MapUtils.getString(parameter, "labels");
//        String localAbsolutePath = MapUtils.getString(parameter, "localFilePath");
//
//        MultipartFile file = MapUtils.getFile(parameter, "file");

        DataSource dataSource = dataSourceService.queryById(dataSourceId);

        // 数据的创建根据数据源的不同，创建方式也不同
        if (type == DbType.HIVE.getIndex()) {
            return dataService.createHiveData(dataSource, loginUser, createMethod, createSql, tableName, columns, name, description, labels);
        } else if (type == DbType.HBASE.getIndex()) {
            return dataService.createHBaseData(dataSource, loginUser, tableName, columns, name, description, labels);
        } else if (type == DbType.HDFS.getIndex()) {
            return dataService.createHDFSData(dataSource, loginUser, file, name, description, labels);
        }
        putMsg(result, Status.UNKOWN_DATASOURCE_TYPE);
        return result;
    }

    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        Data data = dataService.queryById(MapUtils.getInt(parameter, "id"));
        data.setName(MapUtils.getString(parameter, "name"));
        String labels = MapUtils.getString(parameter, "labels");
        String[] ids = labels.replace("[", "").replace("]", "").split(",");
        List<Label> labelList = labelService.queryByDataId(data.getId());
        for(Label label : labelList) {
            labelService.deleteDataLabelRelation(label.getId(), data.getId());
        }
        for(String id: ids) {
            labelService.insertDataLabelRelation(Integer.parseInt(id), data.getId());
        }
        data.setDescription(MapUtils.getString(parameter, "description"));
        data.setUpdateTime(new Date());
        dataService.update(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @PostMapping("/add-column")
    public Result addColumn(@RequestBody Map<String, Object> parameter) {
        int dataId = MapUtils.getInt(parameter, "dataId");
        String columns = MapUtils.getString(parameter, "columns");
        return dataService.addColumn(dataId, columns);
    }

    @GetMapping("/queryById")
    public Result queryById(int id){
        Result result = new Result();
        Data data = dataService.queryById(id);
        result.setData(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/search")
    public Result search(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int type, String labels, int pageNo, int pageSize, String startTime, String endTime) {
        Result result = new Result();
        List<Data> targetDataList = dataService.search(loginUser.getId(), name, type, labels, pageNo, pageSize, startTime, endTime);
        for(Data data : targetDataList) {
            List<Label> labelList = labelService.queryByDataId(data.getId());
            data.setLabelList(labelList);
        }
        int total = dataService.searchTotal(loginUser.getId(), name, type, labels, startTime, endTime);
        result.setData(targetDataList);
        Map resultMap = new HashMap();
        resultMap.put("total", total);
        resultMap.put("pageSize", pageSize);
        result.setDataMap(resultMap);
        putMsg(result, Status.SUCCESS);

        // 记录用户的搜索行为
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(name)){
            int count = dataService.log(loginUser.getId(), name);
            if( count == 0) {
                logger.error("记录查询日志失败！");
            }
        }
        return result;
    }

    /**
     * delete data
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        DataSource dataSource = dataService.queryDataSourceByDataId(id);
        Data data = dataService.queryById(id);
        if(data.getType() == DbType.HIVE.getIndex()) {
            try{
                HiveUtils.dropTable(dataSource, data.getDataName());
            } catch (SQLException e) {
                putMsg(result, Status.HIVE_DROP_TABLE_FAILED);
                logger.error("删除Hive表失败\n" + e.getMessage());
                return result;
            } catch (ClassNotFoundException e) {
                putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
                logger.error("删除Hive数据表失败\n" + e.getMessage());
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

    @GetMapping("/preview")
    public Result preview(int dataId) {
        Data data = dataService.queryById(dataId);
        if(data.getType() == DbType.HDFS.getIndex()) {
            return dataService.hdfsPreview(dataId);
        } else {
            return dataService.tablePreview(dataId);
        }
    }

    @GetMapping("/listByDataSource")
    public Result listByDataSource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataSourceId, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        if(pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        if(pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        if(dataSourceId == null) {
            dataSourceId = 0;
        }
        List<Data> dataList = dataService.list(loginUser.getId(), dataSourceId, pageNo, pageSize);
        int total = dataService.listTotal(loginUser.getId(), dataSourceId);
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

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity download(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataId, String condition) {
        // 根据下载的文件类型来决定下载下来的格式，如果是HBase/Hive数据，下载成cvs文件，如果是HDFS文件，则直接从HDFS上拉取
        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
        if(data.getType() == DbType.HDFS.getIndex()) {
            // 直接从HDFS上下载文件
            String hdfsFileName = dataSource.getCategory1() + File.separator + data.getDataName();
            String localFileName = FileUtils.getDownloadFilename(data.getDataName());
            try{
                HDFSUtils.copyHdfsToLocal(hdfsFileName, localFileName, false, true);
            } catch (IOException e) {
                logger.error("从HDFS上下载文件到本地失败\n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("HDFS文件下载失败");
            }

            try {
                org.springframework.core.io.Resource file = FileUtils.file2Resource(localFileName);
                if(file == null){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据文件不存在");
                }
                return ResponseEntity
                            .ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(file.getFilename(),"UTF-8") + "\"")
                            .body(file);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件下载出错");
            }
        } else {
            List<String> lineList = null;
            if(data.getType() == DbType.HIVE.getIndex()) {
                try{
                    lineList = HiveUtils.downloadTableData(dataSource, data.getDataName(), condition, 0);
                } catch (SQLException e) {
                    logger.error("读取Hive表数据失败\n" + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取Hive表数据失败");
                } catch (ClassNotFoundException e) {
                    logger.error("数据驱动加载失败\n" + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("数据驱动加载失败");
                }

            } else {
                try{
                    lineList = HBaseUtils.downloadTableData(dataSource, data.getDataName(), Constants.maxDownloadRecord);
                } catch (IOException e) {
                    logger.error("读取HBase数据失败\n" + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取HBase数据失败");
                }
            }
            String localFileName = FileUtils.getDownloadFilename(data.getName()) + ".csv";
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
    }
}
