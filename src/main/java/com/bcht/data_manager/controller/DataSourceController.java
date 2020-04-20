package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

        Result result = new Result();
        DataSource dataSource = new DataSource();
        dataSource.setIp(ip);
        dataSource.setCategory1(category1);
        dataSource.setName(name);
        dataSource.setPort(port);
        dataSource.setType(type);
        dataSource.setDescription(description);
        dataSource.setCreatorId(loginUser.getId());
        dataSourceService.insert(dataSource);
        result.setMsg("新增数据源成功");
        result.setCode(0);
        return result;
    }

    /**
     * update datasource
     */
    @PostMapping("/update")
    public Result update(int id, String name, int type, String ip, int port, String category1, String description) {
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryById(id);
        dataSource.setName(name);
        dataSource.setType(type);
        dataSource.setIp(ip);
        dataSource.setPort(port);
        dataSource.setCategory1(category1);
        dataSource.setDescription(description);
        dataSourceService.update(dataSource);
        putMsg(result, Status.SUCCESS);
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

    /**
     * query datasource by name
     */
    @GetMapping("/queryByName")
    public Result queryByName(String name){
        Result result = new Result();
        DataSource dataSource = dataSourceService.queryByName(name);
        result.setData(dataSource);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/queryByUser")
    public Result queryByUser(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        int userId = loginUser.getId();
        List<DataSource> dataSourceList = dataSourceService.queryByUserId(userId);
        for(DataSource dataSource: dataSourceList) {
                dataSource.setKey(dataSource.getId());
        }
        result.setData(dataSourceList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * insert datasource
     */
    @PostMapping("/testConnection")
    public Result testConnection(String name, int type, String ip, int port, String category1, String description) {

        Result result = new Result();
        DataSource dataSource = new DataSource();
        dataSource.setIp(ip);
        dataSource.setCategory1(category1);
        dataSource.setName(name);
        dataSource.setPort(port);
        dataSource.setType(type);
        dataSource.setDescription(description);
        dataSourceService.insert(dataSource);
        result.setMsg("新增数据源成功");
        result.setCode(0);
        return result;
    }

    /**
     * delete datasource
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        dataSourceService.deleteById(id);
        result.setMsg("删除数据源成功");
        result.setCode(0);
        return result;
    }



}