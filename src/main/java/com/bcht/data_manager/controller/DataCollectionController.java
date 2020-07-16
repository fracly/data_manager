package com.bcht.data_manager.controller;

import com.alibaba.fastjson.JSON;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.*;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.CollectionMapper;
import com.bcht.data_manager.service.CollectionService;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.bcht.data_manager.consts.Constants.*;

@RestController
@RequestMapping("/api/data-collection")
public class DataCollectionController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataCollectionController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private CollectionService collectionService;

    private static DatagramSocket serverSocket = null;

    private static String cacheIp = "";

    private static Integer cachePort = 0;

    private static List<Rule> cacheRules = new ArrayList<>();

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
                    jdbcUrl = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
                    break;
                case 2:
                    Class.forName(COM_ORACLE_JDBC_DRIVER);
                    jdbcUrl = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + database + "?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
                    break;
                case 3:
                    Class.forName(COM_DB2_JDBC_DRIVER);
                    jdbcUrl = "jdbc:db2://" + ip + ":" + port + "/" + database + "?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
                    break;
                case 4:
                    Class.forName(COM_SQLSERVER_JDBC_DRIVER);
                    jdbcUrl = "jdbc:sqlserver://" + ip + ":" + port + "/" + database + "?characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
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
            collectionService.insert(job);
            return result;
        }
        collectionService.insert(job);
        putMsg(result, Status.CUSTOM_SUCESSS, "sqoop 脚本执行成功");
        return result;
    }

    @PostMapping("/file")
    public Result file(@RequestAttribute(value = SESSION_USER) User loginUser, @RequestParam("file") MultipartFile file, Long dataId, boolean overwrite) {
        Result result = new Result();
        if (file == null ) {
            putMsg(result, Status.FAILED);
            return result;
        }

        Data data = dataService.queryById(dataId);
        DataSource dataSource = dataService.queryDataSourceByDataId(dataId);

        String fileName = file.getOriginalFilename();
        String localFileName = FileUtils.getUploadFilename(file.getOriginalFilename());
        String tableName = data.getDataName();

        Job job = new Job();
        job.setCreatorId(loginUser.getId());
        job.setInputType(FILE);
        job.setOutputId(dataId);
        job.setOutputType(DbType.HIVE.getIndex());
        job.setStartTime(new Date());
        job.setType(FILE2HIVE);
        job.setOutputName(data.getDataName());

        try {
            FileUtils.copyFile(file, localFileName);
        } catch (IOException e) {
            logger.error("文件拷贝失败,从MultipleFile到Local" + e.getMessage());
            putMsg(result, Status.CUSTOM_FAILED, "写入本地出错");
            return result;
        }

        List<String> commentNameList = null;
        try {
            commentNameList = HiveUtils.getTableColumnNameList(dataSource, tableName);
        } catch (Exception e) {
            logger.error("获取数据的字段列表失败:" + e.getMessage());
            putMsg(result, Status.CUSTOM_FAILED, "获取数据的字段列表失败");
            job.setStatus(Constants.FAILED);
            return result;
        }

        if (commentNameList != null ) {
            HashMap<String, Integer> realMap = collectionService.getRealMap(commentNameList);
            String alteredContent;
            File tmpFile = new File(localFileName);
            try{
                alteredContent = collectionService.transformFile2String(tmpFile, realMap);
            }catch (IOException e) {
                logger.error("转化出错:" + e.getMessage());
                putMsg(result, Status.CUSTOM_FAILED, "备注名不匹配");
                job.setStatus(Constants.FAILED);
                return result;
            }

            if(alteredContent != null) {
                File dest = new File(localFileName);
                if(!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }

                collectionService.saveAsFileWriter(alteredContent, localFileName);

                try{
                    HDFSUtils.copyLocalToHdfs(localFileName, "/tmp/" + fileName,true, overwrite);
                    HiveUtils.loadDataFromHdfsFile(dataSource, tableName, overwrite, "/tmp/" + fileName);
                } catch (Exception e) {
                    logger.error("hdfs上传加载文件失败:" + e.getMessage());
                    putMsg(result, Status.CUSTOM_FAILED, "hdfs上传加载文件失败");
                    job.setStatus(Constants.FAILED);
                    return result;
                }

            }else {
                putMsg(result, Status.CUSTOM_FAILED, "字段备注与表名不匹配");
                job.setStatus(Constants.FAILED);
                return result;
            }
        } else{
            putMsg(result, Status.CUSTOM_FAILED, "获取列名集合失败");
            job.setStatus(Constants.FAILED);
            return result;
        }

        job.setStatus(FINISHED);
        collectionService.insert(job);
        putMsg(result, Status.CUSTOM_SUCESSS, "文件导入成功");
        return result;

    }

    @GetMapping("/job-list")
    public Result jobList(@RequestAttribute(value = SESSION_USER) User loginUser, int status) {
        Result result = new Result();
        List<Job> list = collectionService.jobList(loginUser.getId(), status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/job-delete")
    public Result jobDelete(@RequestAttribute(value = SESSION_USER) User loginUser, int id) {
        Result result = new Result();
        int count = collectionService.jobDelete(id);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "删除成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "删除失败");
        }
        return result;
    }

    @PostMapping("/start-auto")
    public Result startAutoJob(@RequestAttribute(value = SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();
        Integer port = MapUtils.getInt(parameter, "port");
        String ip = MapUtils.getString(parameter, "ip");
        String rulesJson = MapUtils.getString(parameter, "rulesJson");
        DatagramPacket packet;
        byte[] data = new byte[1024 * 64]; // 一次最多传输64KB的数据
        try{
            serverSocket = new DatagramSocket(port);
            // 先将服务的配置信息保存起来
            cacheIp = ip;
            cachePort = port;
            List<Rule> ruleList = JSON.parseArray(rulesJson, Rule.class);
            cacheRules = ruleList;
            // 开始接受数据
            while(true) {
                packet = new DatagramPacket(data, data.length);
                serverSocket.receive(packet);
                // 针对接受到packet做存储,每天规则进行解析
                byte[] dataBytes = packet.getData();
                for(Rule rule: cacheRules) {
                    int offset = rule.getOffset();
                    int length = rule.getLength();
                    String value = rule.getValue();
                    byte[] potentialFunctionValue = Arrays.copyOfRange(dataBytes, offset, offset + length);
                    String potentalHexValue = StringUtils.byteToHex(potentialFunctionValue);
                    System.out.println(potentalHexValue);
                    if(potentalHexValue.equals(value)) {
                        Data dataObj = dataService.queryById(rule.getTarget());
                        DataSource dataSourceObj = dataService.queryDataSourceByDataId(rule.getTarget());
                        byte[] realData = Arrays.copyOfRange(dataBytes, 0, packet.getLength());
                        // 将数据直接写入HBase中
                        HBaseUtils.insertUDPPacket(dataSourceObj, dataObj.getDataName(), rule, realData);
                    }else {
                        continue;
                    }
                }
                System.out.println(packet.getLength());
                System.out.println(new String(packet.getData(), 0, packet.getLength()));
            }
        }catch (Exception e) {
            e.printStackTrace();
            putMsg(result, Status.CUSTOM_FAILED, "端口已被占用！请重新输入");
            return result;
        }
    }

    @GetMapping("/stop-auto")
    public Result stopAutoJob() {
        Result result = new Result();
        serverSocket.close();
        putMsg(result, Status.CUSTOM_SUCESSS, "停止UDPServer成功");
        return result;
    }

    @GetMapping("/init-auto")
    public Result initAutoJob() {
        Result result = new Result();
        Map map = new HashMap();
        if(serverSocket == null || serverSocket.isClosed()) {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                cacheIp = addr.getHostAddress();
                cachePort = null;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        map.put("ip", cacheIp);
        map.put("port", cachePort);
        result.setData(cacheRules);
        result.setDataMap(map);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/target-list")
    public Result targets(@RequestAttribute(value = SESSION_USER) User loginUser) {
        Result result = new Result();
        List<Data> list = dataService.queryByType(loginUser.getId(), DbType.HBASE.getIndex());
        result.setData(list);
        putMsg(result, Status.SUCCESS);
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
