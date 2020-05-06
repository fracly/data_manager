package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/data-collection")
public class DataCollectionController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataCollectionController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private CollectionMapper collectionMapper;

    @PostMapping("/test")
    public Result test(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();
        String type = MapUtils.getString(parameter, "type");
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
                case Constants.MYSQL:
                    Class.forName(Constants.COM_MYSQL_JDBC_DRIVER);
                    jdbcUrl = "jdbc:mysql://" + ip + ":" + port + "/" + database;
                    break;
                case Constants.ORACLE:
                    Class.forName(Constants.COM_ORACLE_JDBC_DRIVER);
                    jdbcUrl = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + database;
                    break;
                case Constants.DB2:
                    Class.forName(Constants.COM_DB2_JDBC_DRIVER);
                    jdbcUrl = "jdbc:db2://" + ip + ":" + port + "/" + database;
                    break;
                case Constants.SQLSERVER:
                    Class.forName(Constants.COM_SQLSERVER_JDBC_DRIVER);
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
    public Result sqoop(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        // 参数获取
        String inputType = MapUtils.getString(parameter, "inputType");
        String outputType = MapUtils.getString(parameter, "outputType");

        String ip = MapUtils.getString(parameter, "ip");
        int port = MapUtils.getInt(parameter, "port");
        String database = MapUtils.getString(parameter, "database");
        String table = MapUtils.getString(parameter, "table");
        String username = MapUtils.getString(parameter, "username");
        String password = MapUtils.getString(parameter, "password");

        String command = SqoopUtils.importRDBSToHive(inputType, ip, port, database, table, username, password);

        Process process = null;
        StringBuilder output = new StringBuilder();
        try{
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            putMsg(result, Status.EXECUTE_SHELL_FAILED);
            logger.error("sqoop 脚本执行失败！");
            return result;
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "sqoop 脚本执行成功");
//        if(inputType.equals(Constants.MYSQL)) {
//
//        } else if(inputType.equals(Constants.ORACLE)) {
//
//        } else if(inputType.equals(Constants.SQLSERVER)) {
//
//        } else if(inputType.equals(Constants.DB2)) {
//
//        }


        return result;
    }

    @PostMapping("/file")
    public Result file(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        MultipartFile file = MapUtils.getFile(parameter, "file");
        int dataId = MapUtils.getInt(parameter, "dataId");

        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);

        String localFileName = FileUtils.getUploadFilename(file.getName());

        File localFile =  new File(localFileName);
        try{
            file.transferTo(localFile);
        } catch (IOException e) {
            putMsg(result, Status.FILE_UPLOAD_FAILED);
            logger.error("文件写入本地失败");
            return result;
        }

        if(data.getType() == DbType.HIVE.getIndex()) {
            try{
                HiveUtils.loadDataFromLocalFile(dataSource, data.getDataName(), localFileName);
            } catch (Exception e) {
                putMsg(result, Status.LOAD_LOCAL_FILE_TO_HIVE_TABLE_FAILED);
                logger.error("load data local 到Hive表失败，请查询原因" + e.getMessage());
                return result;
            }
        }
        return result;
    }

    @GetMapping("/job-list")
    public Result jobList(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int status) {
        Result result = new Result();
        List<Map<String, Object>> list = collectionMapper.jobList(loginUser.getId(), name, status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }
}
