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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据Controller
 * 对应数据的CRUD、下载等操作，以及花式查询，数据分为3类
 *      1、Hive表，对应表的增删改查，不加载数据。
 *      2、HBase表，暂无实现
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
    private LabelService labelService;

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * 数据新增
     */
    @PostMapping("/create")
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer createMethod, Integer type, Long dataSourceId, String name,
                         String columns, String tableName, String createSql, String description, String labels, String fileName, Integer status) {
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(dataSourceId);

        // 数据的创建根据数据源的不同，创建方式也不同
        if (type == DbType.HIVE.getIndex()) {
            return dataService.createHiveData(dataSource, loginUser, createMethod, createSql, tableName, columns, name, description, labels, status);
        } else if (type == DbType.HBASE.getIndex()) {
            return dataService.createHBaseData(dataSource, loginUser, tableName, columns, name, description, labels, status);
        } else if (type == DbType.HDFS.getIndex()) {
            return dataService.createHDFSData(dataSource, loginUser, fileName, name, description, labels, status);
        }
        putMsg(result, Status.UNKOWN_DATASOURCE_TYPE);
        return result;
    }

    /**
     * 数据删除
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        DataSource dataSource = dataService.queryDataSourceByDataId(id);
        Data data = dataService.queryById(id);
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
    public Result update(@RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        Data data = dataService.queryById(MapUtils.getInt(parameter, "id"));
        data.setName(MapUtils.getString(parameter, "name"));
        String labels = MapUtils.getString(parameter, "labels");
        Integer status = MapUtils.getInt(parameter, "status");
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
        data.setStatus(status);
        dataService.update(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据修改-状态更新
     */
    @PostMapping("updateStatus")
    public Result updateStatus(Integer dataId, Integer status) {
        Result result = new Result();
        Data data = dataService.queryById(dataId);
        data.setStatus(status);
        dataService.update(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据修改-Hive表增加列
     */
    @PostMapping("/add-column")
    public Result addColumn(@RequestBody Map<String, Object> parameter) {
        int dataId = MapUtils.getInt(parameter, "dataId");
        String columns = MapUtils.getString(parameter, "columns");
        return dataService.addColumn(dataId, columns);
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
            int count = dataService.logSearch(loginUser.getId(), name);
            if( count == 0) {
                logger.error("记录查询日志失败！");
            }
        }
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
     * 数据查询-预览前N行数据
     */
    @GetMapping("/preview")
    public Result preview(int dataId) {
        Data data = dataService.queryById(dataId);
        if(data.getType() == DbType.HDFS.getIndex()) {
            return dataService.hdfsPreview(dataId);
        } else {
            return dataService.tablePreview(dataId);
        }
    }

    /**
     * 数据查询-根据数据源查询
     */
    @GetMapping("/listByDataSource")
    public Result listByDataSource(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataSourceId, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        if(pageNo == null || pageNo == 0) { pageNo = 1; }
        if(pageSize == null || pageSize == 0) { pageSize = 10; }
        if(dataSourceId == null) { dataSourceId = 0; }

        List<Data> dataList = dataService.list(loginUser.getId(), dataSourceId, pageNo, pageSize);
        for(Data data : dataList) {
            List<Label> labelList = labelService.queryByDataId(data.getId());
            data.setLabelList(labelList);
            DataSource dataSource = dataService.queryDataSourceByDataId(data.getId());
            data.setDataSourceId(dataSource.getId());
            data.setDataSourceName(dataSource.getName());
        }
        result.setData(dataList);

        Map<String, Object> map = new HashMap<>();
        int total = dataService.listTotal(loginUser.getId(), dataSourceId);
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
    public ResponseEntity download(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer dataId, String condition) {
        // 根据文件类型判断处理逻辑：
        //  HBase/Hive数据，下载成cvs文件，返回生成 ResponseEntity
        Data data = dataService.queryById(dataId);

        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
        List<String> lineList = null;
        if(data.getType() == DbType.HIVE.getIndex()) {
            try{
                lineList = HiveUtils.downloadTableData(dataSource, data.getDataName(), condition, 0);
            } catch (Exception e) {
                logger.error("下载Hive表数据失败\n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取Hive表数据失败");
            }
        } else if(data.getType() == DbType.HBASE.getIndex()){
            try{
                lineList = HBaseUtils.downloadTableData(dataSource, data.getDataName(), Constants.maxDownloadRecord);
            } catch (IOException e) {
                logger.error("下载HBase数据失败\n" + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("读取HBase数据失败");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件类型错误！");
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
