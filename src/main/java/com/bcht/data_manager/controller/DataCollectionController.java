package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Job;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.CollectionMapper;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import static com.bcht.data_manager.consts.Constants.*;

@RestController
@RequestMapping("/api/data-collection")
public class DataCollectionController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataCollectionController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private CollectionMapper collectionMapper;

    @PostMapping("/test")
    public Result test(@RequestAttribute(value = SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();
        int type = MapUtils.getInt(parameter, "inputType");
        String ip = MapUtils.getString(parameter, "ip");
        int port = MapUtils.getInt(parameter, "port");
        String username = MapUtils.getString(parameter, "username");
        String password = MapUtils.getString(parameter, "password");
        String database = MapUtils.getString(parameter, "database");
        String table = MapUtils.getString(parameter, "table");

        String jdbcUrl = null;
        Connection connection ;
        try{
            switch (type) {
                case 1:
                    Class.forName(COM_MYSQL_JDBC_DRIVER);
                    jdbcUrl = "jdbc:mysql://" + ip + ":" + port + "/" + database;
                    break;
                case 2:
                    Class.forName(COM_ORACLE_JDBC_DRIVER);
                    jdbcUrl = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + database;
                    break;
                case 3:
                    Class.forName(COM_DB2_JDBC_DRIVER);
                    jdbcUrl = "jdbc:db2://" + ip + ":" + port + "/" + database;
                    break;
                case 4:
                    Class.forName(COM_SQLSERVER_JDBC_DRIVER);
                    jdbcUrl = "jdbc:sqlserver://" + ip + ":" + port + "/" + database;
                    break;
            }
            Set<String> tableSet = new HashSet<>();
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("show tables");
            while(rs.next()) {
                tableSet.add(rs.getString(1));
            }
            if(tableSet.contains(table)) {
                putMsg(result, Status.CUSTOM_SUCESSS, "连接测试成功");
                return result;
            } else {
                putMsg(result, Status.NOT_FOUND_TABLE_IN_THIS_DATABASE);
                return result;
            }

        } catch (ClassNotFoundException e) {
            putMsg(result, Status.MYSQL_JDBC_DRIVER_CLASS_NOT_FOUNT);
            logger.error("连接测试失败" + e.getMessage());
            return result;
        } catch (SQLException e) {
            putMsg(result, Status.GET_INPUT_DATASOURCE_FAILED);
            logger.error("获取JDBC连接失败" + e.getMessage());
            return result;
        }
    }

    @PostMapping("/sqoop")
    public Result sqoop(@RequestAttribute(value = SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        // 参数获取
        int inputType = MapUtils.getInt(parameter, "inputType");
        String outputType = MapUtils.getString(parameter, "outputType");
        long outputId = MapUtils.getLong(parameter, "outputId");

        String ip = MapUtils.getString(parameter, "ip");
        int port = MapUtils.getInt(parameter, "port");
        String database = MapUtils.getString(parameter, "database");
        String table = MapUtils.getString(parameter, "table");
        String username = MapUtils.getString(parameter, "username");
        String password = MapUtils.getString(parameter, "password");

        Data data = dataService.queryById(outputId);
        DataSource dataSource = dataService.queryDataSourceByDataId(outputId);

        String command = SqoopUtils.importRDBSToHive(inputType, ip, port, database, table, username, password, dataSource.getCategory1(), data.getDataName());

        // 记录执行历史
        Job job = new Job();
        job.setCreatorId(loginUser.getId());
        job.setInputParameter(parameter.toString());
        job.setInputType(inputType);
        job.setOutputId(outputId);
        job.setOutputType(DbType.HIVE.getIndex());
        job.setStartTime(new Date());
        job.setType(DB2HIVE);
        job.setOutputName(data.getDataName());

        Process process = null;
        StringBuilder output = new StringBuilder();
        try{
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            putMsg(result, Status.EXECUTE_SHELL_FAILED);
            logger.error("sqoop 脚本执行失败！");
            job.setStatus(FAILED);
            collectionMapper.insert(job);
            return result;
        }
        job.setStatus(RUNNING);
        collectionMapper.insert(job);
        putMsg(result, Status.CUSTOM_SUCESSS, "sqoop 脚本执行成功");
        return result;
    }

    @PostMapping("/file")
    public Result file(@RequestAttribute(value = SESSION_USER) User loginUser, MultipartFile file, Long dataId, Integer inputType) {
        Result result = new Result();


        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);

        Job job = new Job();
        job.setCreatorId(loginUser.getId());
        job.setInputType(FILE);
        job.setOutputId(dataId);
        job.setOutputType(DbType.HIVE.getIndex());
        job.setStartTime(new Date());
        job.setType(FILE2HIVE);
        job.setOutputName(data.getDataName());

        String localFileName = FileUtils.getUploadFilename(file.getOriginalFilename());

        File localFile =  new File(localFileName);
        try{
            if(!localFile.getParentFile().exists()){
                localFile.getParentFile().mkdirs();
            }
            file.transferTo(localFile);
        } catch (IOException e) {
            putMsg(result, Status.FILE_UPLOAD_FAILED);
            job.setStatus(FAILED);
            collectionMapper.insert(job);
            logger.error("文件写入本地失败" + e.getMessage());
            return result;
        }

        if(data.getType() == DbType.HIVE.getIndex()) {
            try{
                HiveUtils.loadDataFromLocalFile(dataSource, data.getDataName(), localFileName);
            } catch (Exception e) {
                putMsg(result, Status.LOAD_LOCAL_FILE_TO_HIVE_TABLE_FAILED);
                logger.error("load data local 到Hive表失败，请查询原因" + e.getMessage());
                job.setStatus(FAILED);
                collectionMapper.insert(job);
                return result;
            }
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "暂不支持除Hive表之外的数据导入！");
        }
        job.setStatus(FINISHED);
        collectionMapper.insert(job);
        return result;
    }

    @GetMapping("/job-list")
    public Result jobList(@RequestAttribute(value = SESSION_USER) User loginUser, int status) {
        Result result = new Result();
        List<Job> list = collectionMapper.jobList(loginUser.getId(), status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/job-delete")
    public Result jobDelete(@RequestAttribute(value = SESSION_USER) User loginUser, int id) {
        Result result = new Result();
        int count = collectionMapper.jobDelete(id);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除失败");
        }
        return result;
    }

}
