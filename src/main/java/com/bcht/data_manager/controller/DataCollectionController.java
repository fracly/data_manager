package com.bcht.data_manager.controller;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Job;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.CollectionMapper;
import com.bcht.data_manager.service.CollectionService;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.utils.*;

import io.netty.util.internal.StringUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;


import static com.bcht.data_manager.consts.Constants.*;
import static com.bcht.data_manager.service.CollectionService.saveAsFileWriter;
import static com.bcht.data_manager.utils.HiveUtils.getTableColumnNameList;

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
        boolean overwrite = MapUtils.getBoolean(parameter, "overwrite");

        Data data = dataService.queryById(outputId);
        DataSource dataSource = dataService.queryDataSourceByDataId(outputId);

        String command = SqoopUtils.importRDBSToHive(inputType, ip, port, database, table, username, password, dataSource.getCategory1(), data.getDataName(), overwrite);

        System.out.println(command);
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
            OutputProcessor error = new OutputProcessor(process.getErrorStream());
            OutputProcessor input = new OutputProcessor(process.getInputStream());
            error.start();
            input.start();
            int exitCode = process.waitFor();
            if(exitCode == 0) {
                job.setStatus(FINISHED);
            } else {
                job.setStatus(FAILED);
            }
        } catch (IOException | InterruptedException e) {
            putMsg(result, Status.EXECUTE_SHELL_FAILED);
            logger.error("sqoop 脚本执行失败！");
            job.setStatus(FAILED);
            collectionMapper.insert(job);
            return result;
        }
        collectionMapper.insert(job);
        putMsg(result, Status.CUSTOM_SUCESSS, "sqoop 脚本执行成功");
        return result;
    }


    @PostMapping("/file")
    public Result file(@RequestAttribute(value = SESSION_USER) User loginUser, @RequestParam("file") MultipartFile file, Long dataId, boolean overwrite) {
        Result result = new Result();
        if(!file.isEmpty()){
            try{
                /**
                 *根据dataId查找data,数据库,表
                 */
                Data data = dataService.queryById(dataId);
                DataSource dataSource = dataService.queryDataSourceByDataId(dataId);
                String fileName = file.getName();
                String tableName = data.getDataName(); //通过data拿到表名
                logger.info("上传的文件名为: " + fileName);
                /**
                 *将上传的文件复制到本地:先拿到上传文件名称再拿到上传文件路径,通过一个getUploadFilename函数就可以完成功能
                 */
                String localFileName = FileUtils.getUploadFilename(file.getOriginalFilename());
                File localfile = new File(localFileName);
                try {
                    FileUtils.copyFile(file, localFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /**
                 *开始对文件进行操作
                 */
                try{
                    List<String> commentNameList = getTableColumnNameList(dataSource, tableName);//拿到字段备注
                    System.out.println(commentNameList);
                    /**
                     *拿到对应参数,通过相应参数,建立Map映射表,执行数据转换操作
                     */
                    StringBuilder fileResult = new StringBuilder();//用作输出数据
                    HashMap<String, Integer> realMap = CollectionService.getRealMap(commentNameList);
                    HashMap<Integer, Integer> alterMap = new HashMap();
                    String txt = CollectionService.txt2String(fileResult, localfile, realMap, alterMap); //作为最终转换后的txt内容
                    /**
                     *数据转换完成,将内容写入至新的txt文件,并保存在tmp目录
                     */

                    File dest = new File(localFileName);
                    if(!dest.getParentFile().exists()) {
                        dest.getParentFile().mkdirs(); //新建文件夹
                    }
                    saveAsFileWriter(txt, localFileName);
                    String hdfsPath = "/tmp/" + fileName;
                    HDFSUtils.copyLocalToHdfs(localFileName,hdfsPath,true, overwrite);
                    HiveUtils.loadDataFromHdfsFile(dataSource,tableName,overwrite,hdfsPath);
                    Job job = new Job();
                    job.setCreatorId(loginUser.getId());
                    job.setInputType(FILE);
                    job.setOutputId(dataId);
                    job.setOutputType(DbType.HIVE.getIndex());
                    job.setStartTime(new Date());
                    job.setType(FILE2HIVE);
                    job.setOutputName(data.getDataName());
                    job.setStatus(FINISHED);
                    collectionMapper.insert(job);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        putMsg(result,Status.SUCCESS);
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

class OutputProcessor extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputProcessor.class);
    private InputStream inputStream;

    public OutputProcessor(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                LOGGER.info("{}", line);
            }
        } catch (Exception e) {
            LOGGER.error("", e.getMessage(), e);
        }
    }
}
