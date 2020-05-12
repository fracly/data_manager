package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.HBaseUtils;
import com.bcht.data_manager.utils.HDFSUtils;
import com.bcht.data_manager.utils.HiveUtils;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * 数据源Controller
 * 对应数据源的CRUD，数据源跟数据一样，分为3类
 *      1、Hive源 - 一个源对应Hive的数据库概念
 *      2、HBase源 - 一个源对应Hbase命名空间的概念
 *      3、HDFS源- 一个源对应HDFS一个目录的概念
 *
 *  @author fracly
 *  @date 2020-05-12 17:27:00
 */
@RestController
@RequestMapping("/api/datasource")
public class DataSourceController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private DataSourceService dataSourceService;


    /**
     * 新增数据源
     */
    @PostMapping("/create")
    public Result insert(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int type, String ip, int port, String category1, String description) {
        logger.info("user {} is creating datasource using name {}", loginUser.getUsername(), name);
        Result result = new Result();

        DataSource dataSource = new DataSource();
        dataSource.setIp(ip);
        dataSource.setCategory1(category1);
        dataSource.setName(name);
        dataSource.setPort(port);
        dataSource.setType(type);
        dataSource.setDescription(description);
        dataSource.setCreatorId(loginUser.getId());

        //先进行实际的物理操作，再对应的源创建相应的命名空间
        if(type == DbType.HIVE.getIndex()) {
            try {
                HiveUtils.createDatabase(dataSource, category1);
            } catch (Exception e) {
                putMsg(result, Status.HIVE_CREATE_DATABASE_FAILED);
                logger.error("创建Hive数据库失败\n" + e.getMessage());
                return result;
            }
        } else if(type == DbType.HBASE.getIndex()) {
            try{
                HBaseUtils.createNameSpace(dataSource, category1);
            } catch (IOException e) {
                putMsg(result, Status.HBASE_CREATE_NAMESPACE_FAILED);
                logger.error("创建Hbase表空间失败\n" + e.getMessage());
                return result;
            }
        } else if(type == DbType.HDFS.getIndex()) {
            try{
                HDFSUtils.mkdir(category1);
            } catch (IOException e) {
                putMsg(result, Status.HDFS_CREATE_DIRECTORY_FAILED);
                logger.error("创建HDFS目录失败\n" + e.getMessage());
                return result;
            }
        }
        // 再保存数据源信息
        int count = dataSourceService.insert(dataSource);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建数据源成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建数据源失败");
        }
        return result;
    }

    /**
     * 修改数据源
     */
    @PostMapping("/update")
    public Result update(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,  int id, String name, int type, String ip, int port, String category1, String description) {
        logger.info("user {} is updating datasource using name {}",  loginUser.getUsername(), name);
        Result result = new Result();

        DataSource dataSource = dataSourceService.queryById(id);
        if (!category1.equals(dataSource.getCategory1())) {
            // 先删表，再建新表
            if (type == DbType.HIVE.getIndex()) {
                try{
                    HiveUtils.dropDatabase(dataSource, dataSource.getCategory1());
                } catch (Exception e) {
                    putMsg(result, Status.HIVE_DROP_DATABASE_FAILED);
                    logger.error("删除Hive数据库失败\n" + e.getMessage());
                    return result;
                }
                try{
                    HiveUtils.createDatabase(dataSource, category1);
                } catch (Exception e) {
                    putMsg(result, Status.HIVE_CREATE_DATABASE_FAILED);
                    logger.error("创建Hive数据库失败\n" + e.getMessage());
                    return result;
                }
            } else if (type == DbType.HBASE.getIndex()) {
                try{
                    HBaseUtils.dropNameSpace(dataSource, dataSource.getCategory1());
                } catch (IOException e) {
                    putMsg(result, Status.HBASE_DROP_NAMESPACE_FAILED);
                    logger.error("删除Hbase表空间失败\n" + e.getMessage());
                    return result;
                }
                try{
                    HBaseUtils.createNameSpace(dataSource, category1);
                } catch (IOException e) {
                    putMsg(result, Status.HBASE_CREATE_NAMESPACE_FAILED);
                    logger.error("创建Hbase表空间失败\n" + e.getMessage());
                    return result;
                }
            } else if (type == DbType.HDFS.getIndex()) {
                try{
                    HDFSUtils.rmdir(dataSource.getCategory1());
                } catch (IOException e) {
                    putMsg(result, Status.HDFS_REMOVE_DIRECTORY_FAILED);
                    logger.error("删除HDFS目录失败\n" + e.getMessage());
                    return result;
                }
                try{
                    HDFSUtils.mkdir(category1);
                } catch (IOException e) {
                    putMsg(result, Status.HDFS_CREATE_DIRECTORY_FAILED);
                    logger.error("创建HDFS目录失败\n" + e.getMessage());
                    return result;
                }
            }
        }

        dataSource.setName(name);
        dataSource.setType(type);
        dataSource.setIp(ip);
        dataSource.setPort(port);
        dataSource.setCategory1(category1);
        dataSource.setDescription(description);
        int count = dataSourceService.update(dataSource);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "更新数据源成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "更新数据源失败");
        }
        return result;
    }

    /**
     * 查询数据源-通过ID
     */
    @GetMapping("/queryById")
    public Result queryById(int id){
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(id);
        result.setData(dataSource);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 查询数据源-通过类型和名称匹配
     */
    @GetMapping("/query")
    public Result queryByUser(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, int type, String name) {
        Result result = new Result();
        int userId = loginUser.getId();
        List<DataSource> dataSourceList = dataSourceService.query(userId, type, name);
        for(DataSource dataSource: dataSourceList) {
                dataSource.setKey(dataSource.getId());
        }
        result.setData(dataSourceList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据源可用性测试
     */
    @PostMapping("/test")
    public Result test(int type, String ip, int port, String category1) {
        return dataSourceService.testConnection(type, ip, port, category1);
    }

    /**
     * 删除数据源
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(id);
        if (dataSource.getType() == DbType.HIVE.getIndex()) {
            try{
                HiveUtils.dropDatabase(dataSource);
            } catch (Exception e) {
                putMsg(result, Status.HIVE_DROP_DATABASE_FAILED);
                logger.error("删除Hive数据库失败\n" + e.getMessage());
                return result;
            }
        } else if (dataSource.getType() == DbType.HBASE.getIndex()) {
            try{
                HBaseUtils.dropNameSpace(dataSource, dataSource.getCategory1());
            } catch (IOException e) {
                putMsg(result, Status.HBASE_DROP_NAMESPACE_FAILED);
                logger.error("删除Hbase表空间失败\n" + e.getMessage());
                return result;
            }
        } else if (dataSource.getType() == DbType.HDFS.getIndex()) {
            try{
                HDFSUtils.rmdir(dataSource.getCategory1());
            } catch (IOException e) {
                putMsg(result, Status.HDFS_REMOVE_DIRECTORY_FAILED);
                logger.error("删除HDFS目录失败\n" + e.getMessage());
                return result;
            }
        }
        dataSourceService.deleteById(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除数据源成功");
        return result;
    }

    /**
     * 数据源查询-按类型统计
     */
    @GetMapping("/statistic")
    public Result statistic(@RequestAttribute(value = Constants.SESSION_USER) User loginUser){
        Result result = new Result();
        List<Map<String, Integer>> mapList = dataSourceService.statistic(loginUser.getId());
        Map<String, Integer> resultMap = new HashMap();
        for(Map<String, Integer> map : mapList) {
            resultMap.put("type" + map.get("type"), map.get("total"));
        }
        result.setData(resultMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 数据源查询-返回树形结构
     */
    @GetMapping("/tree")
    public Result tree(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, Integer type) {
        Result result = new Result();
        List<Map<String, Object>> resultList = new ArrayList();
        List<DbType> dbTypeList = new ArrayList<>();

        if(type == null || type == 0) {
            dbTypeList.addAll(Arrays.asList(DbType.values()));
        } else {
            dbTypeList.add(DbType.valueOf(type));
        }

        for(DbType dbType : dbTypeList) {
            Map map = new HashMap();
            map.put("key", "key-0" + dbType.getIndex());
            map.put("title", dbType.getName());
            map.put("value", 1);
            List<DataSource> hiveList = dataSourceService.query(loginUser.getId(), dbType.getIndex(), null);
            List<Map> hiveChildren = new ArrayList<>();
            for (DataSource dataSource : hiveList) {
                Map<String, Object> tmp = new HashMap();
                tmp.put("key", "key-0" + dbType.getIndex() + "-" + dataSource.getId());
                tmp.put("title", dataSource.getName());
                tmp.put("value", dataSource.getId());
                tmp.put("category1", dataSource.getCategory1());
                hiveChildren.add(tmp);
            }
            map.put("children", hiveChildren);
            resultList.add(map);
        }
        result.setData(resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }
}
