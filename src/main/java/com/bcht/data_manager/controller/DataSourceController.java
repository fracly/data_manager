package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/datasource")
public class DataSourceController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceController.class);

    @Autowired
    private DataSourceService dataSourceService;


    /**
     * insert datasource
     */
    @PostMapping("/create")
    public Result insert(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int type, String ip, int port, String category1, String description) {
        logger.info("user {} is creating datasource using name {}", loginUser.getUsername(), name);

        DataSource dataSource = new DataSource();
        dataSource.setIp(ip);
        dataSource.setCategory1(category1);
        dataSource.setName(name);
        dataSource.setPort(port);
        dataSource.setType(type);
        dataSource.setDescription(description);
        dataSource.setCreatorId(loginUser.getId());
        return dataSourceService.insert(dataSource);
    }

    /**
     * update datasource
     */
    @PostMapping("/update")
    public Result update(int id, String name, int type, String ip, int port, String category1, String description) {
        logger.info("user {} is updating datasource using name {}",  name);

        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(id);
        dataSource.setName(name);
        dataSource.setType(type);
        dataSource.setIp(ip);
        dataSource.setPort(port);
        dataSource.setCategory1(category1);
        dataSource.setDescription(description);
        dataSourceService.update(dataSource);
        putMsg(result, Status.CUSTOM_SUCESSS, "更新数据源成功");
        return result;
    }

    /**
     * query datasource by id
     */
    @GetMapping("/queryById")
    public Result queryById(int id){
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(id);
        result.setData(dataSource);
        putMsg(result, Status.SUCCESS);
        return result;
    }

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


    @PostMapping("/test")
    public Result test(int type, String ip, int port, String category1) {
        return dataSourceService.testConnection(type, ip, port, category1);
    }

    /**
     * delete datasource
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        dataSourceService.deleteById(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除数据源成功");
        return result;
    }

    /**
     * statistic all type datasource
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

    @GetMapping("/tree")
    public Result tree(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        List<Map<String, Object>> resultList = new ArrayList();

        for(DbType dbType : DbType.values()) {
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
