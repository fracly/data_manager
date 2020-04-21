package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Label;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.DataService;
import com.bcht.data_manager.service.DataSourceService;
import com.bcht.data_manager.service.LabelService;
import com.bcht.data_manager.utils.HiveUtils;
import com.bcht.data_manager.utils.MapUtils;
import com.bcht.data_manager.utils.Result;
import com.bcht.data_manager.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/data")
public class DataController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    private DataService dataService;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private LabelService labelService;

    @PostMapping("/create")
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        int method = MapUtils.getInt(parameter, "createMethod");
        int type = MapUtils.getInt(parameter, "type");
        int dataSourceId = MapUtils.getInt(parameter, "dataSourceId");

        String name = MapUtils.getString(parameter, "name");
        String columns = MapUtils.getString(parameter, "columns");
        String dataName = MapUtils.getString(parameter, "dataName");
        String createSql = MapUtils.getString(parameter, "createSql");
        String description = MapUtils.getString(parameter, "description");
        String labels = MapUtils.getString(parameter, "labels");

        DataSource dataSource = dataSourceService.queryById(dataSourceId);

        if (method == Constants.CREATE_TABLE_METHOD_OF_CREATE_SQL){
            dataName = StringUtils.getTableName(createSql);
        } else {
            createSql = StringUtils.composeCreateSql(dataSource.getCategory1(), dataName, columns);
        }
        boolean isSuccess = HiveUtils.createTable(dataSource, createSql);
        if(isSuccess) {
            int currentMaxId = dataService.queryMaxId();
            Data data = new Data();
            data.setId(currentMaxId + 1);
            data.setCreateTime(new Date());
            data.setUpdateTime(new Date());
            data.setCreatorId(loginUser.getId());
            data.setName(name);
            data.setStatus(0);
            data.setType(type);
            data.setDataName(dataName);
            data.setDescription(description);
            try{
                dataService.insert(data);
                dataService.insertDataSourceDataRelation(currentMaxId + 1, dataSourceId);
                String[] ids = labels.split(",");
                for(String id : ids){
                    Integer idInt = Integer.parseInt(id);
                    dataService.insertLabelDataRelation(currentMaxId + 1, idInt);
                }
                putMsg(result, Status.SUCCESS);
            } catch(Exception e) {
                logger.error("插入数据资产失败，资产信息：{}， 报错信息：{}", data.toString(), e.getMessage());
                putMsg(result, Status.FAILED);
            }
        } else {
            logger.error("Hive表创建失败, 建表语句：{}" + createSql);
            putMsg(result, Status.FAILED);
        }
        return result;
    }

    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Object> parameter) {
        Result result = new Result();

        Data data = dataService.queryById(MapUtils.getInt(parameter, "id"));
        data.setName(MapUtils.getString(parameter, "name"));
        data.setDescription(MapUtils.getString(parameter, "description"));
        data.setUpdateTime(new Date());
        dataService.update(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/queryById")
    public Result queryById(int id){
        Result result = new Result();
        Data data = dataService.queryById(id);
        result.setData(data);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/queryByName")
    public Result queryByName(String name){
        Result result = new Result();
        List<Data> dataList = dataService.queryByName(name);
        result.setData(dataList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/search")
    public Result search(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, int type, String labels, int pageNo, int pageSize, String startDate, String endDate) {
        Result result = new Result();
        List<Data> targetDataList = dataService.search(loginUser.getId(), name, type, labels, pageNo, pageSize, startDate, endDate);
        for(Data data : targetDataList) {
            List<Label> labelList = labelService.queryByDataId(data.getId());
            data.setLabelList(labelList);
        }
        int total = dataService.searchTotal(loginUser.getId(), name, type, labels, startDate, endDate);
        result.setData(targetDataList);
        Map resultMap = new HashMap();
        resultMap.put("total", total);
        resultMap.put("pageSize", pageSize);
        result.setDataMap(resultMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/queryByUser")
    public Result queryByUser(@RequestAttribute(value = Constants.SESSION_USER) User loginUser) {
        Result result = new Result();
        return result;
    }

    /**
     * insert data
     */
    @PostMapping("/testConnection")
    public Result testConnection(String name, int type, String ip, int port, String category1, String description) {
        Result result = new Result();
        return result;
    }

    /**
     * delete data
     */
    @GetMapping("/delete")
    public Result delete(int id){
        Result result = new Result();
        dataService.deleteById(id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * 获取数据源下所有的数据
     */
    @GetMapping("/listByDataSource")
    public Result listByDataSource(int dataSourceId, int pageNo, int pageSize) {
        Result result = new Result();
        List<Data> dataList = dataService.listByDataSource(dataSourceId, pageNo, pageSize);
        result.setData(dataList);

        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", pageNo);
        map.put("totalCount", dataService.countByDataSource(dataSourceId));
        result.setDataMap(map);

        putMsg(result, Status.SUCCESS);
        return result;
    }
}
