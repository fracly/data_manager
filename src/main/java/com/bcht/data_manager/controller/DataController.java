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
// User loginUser,
    @PostMapping("/create")
    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser,Integer createMethod, Integer type, Integer dataSourceId,  String name, String createSql, String description, String labels) {
//    public Result create(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, @RequestBody Map<String, Object> parameter) {
        Result result = new Result();

//        int method = MapUtils.getInt(parameter, "createMethod");
//        int type = MapUtils.getInt(parameter, "type");
//        int dataSourceId = MapUtils.getInt(parameter, "dataSourceId");
//
//        String name = MapUtils.getString(parameter, "name");
//        String columns = MapUtils.getString(parameter, "columns");
//        String dataName = MapUtils.getString(parameter, "dataName");
//        String createSql = MapUtils.getString(parameter, "createSql");
//        String description = MapUtils.getString(parameter, "description");
//        String labels = MapUtils.getString(parameter, "labels");

        DataSource dataSource = dataSourceService.queryById(dataSourceId);
        String dataName = "";
        if (createMethod == Constants.CREATE_TABLE_METHOD_OF_CREATE_SQL){
            dataName = StringUtils.getTableName(createSql);
        } else {
            createSql = StringUtils.composeCreateSql(dataSource.getCategory1(), dataName, null);
        }
        boolean isSuccess = HiveUtils.createTable(dataSource, createSql);
        if(isSuccess) {
            Long currentMaxId = dataService.queryMaxId();
            Long nextId = (currentMaxId == null ? 0: currentMaxId) + 1;
            Data data = new Data();
            data.setId(nextId);
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
                dataService.insertDataSourceDataRelation(nextId, dataSourceId);
                String[] ids = labels.replace("[", "").replace("]", "").split(",");
                for(String id : ids){
                    Integer idInt = Integer.parseInt(id.trim());
                    dataService.insertLabelDataRelation(nextId, idInt);
                }
                putMsg(result, Status.SUCCESS);
            } catch(Exception e) {
                e.printStackTrace();
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
        dataService.deleteById(id);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    @GetMapping("/detail")
    public Result detail(int dataSourceId, int dataId) {
        Result result = new Result();
        List<Map<String, String>> map = dataService.detail(dataSourceId, dataId);
        result.setData(map);
        putMsg(result, Status.SUCCESS);
        return result;
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
        }
        result.setData(dataList);

        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", pageNo);
        map.put("total", total);
        result.setDataMap(map);

        putMsg(result, Status.SUCCESS);
        return result;
    }
}
