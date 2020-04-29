package com.bcht.data_manager.service;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.DataSourceMapper;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.utils.DateUtils;
import com.bcht.data_manager.utils.HiveUtils;
import com.bcht.data_manager.utils.MapUtils;
import com.bcht.data_manager.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bcht.data_manager.utils.StringUtils.*;

@Service
public class DataService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Autowired
    private SearchMapper searchMapper;

    /**
     * 创建Hive数据，即Hive表
     *      方式1:直接通过SQL的方式执行
     *      方式2: 拼接字段的方式，生成SQL再执行
     */
    public void createHiveData(DataSource dataSource, User loginUser, Integer createWay, String createSql, String tableName, String columns, String name, String description, String labels) {
        if (createWay == Constants.CREATE_TABLE_METHOD_OF_CREATE_SQL){
            tableName = getTableName(createSql);
        } else if (createWay == Constants.CREATE_TABLE_METHOD_OF_COLUMN_COMPOSE){
            createSql = composeCreateSql(dataSource.getCategory1(), tableName, columns);
        }
        HiveUtils.createTable(dataSource, createSql);
        Long currentMaxId = queryMaxId();
        Long nextId = (currentMaxId == null ? 0: currentMaxId) + 1;
        Data data = new Data();
        data.setId(nextId);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        data.setCreatorId(loginUser.getId());
        data.setName(name);
        data.setStatus(0);
        data.setType(DbType.HIVE.getIndex());
        data.setDataName(tableName);
        data.setDescription(description);
        try{
            insert(data);
            insertDataSourceDataRelation(nextId, dataSource.getId());
            String[] ids = labels.replace("[", "").replace("]", "").split(",");
            for(String id : ids){
                Integer idInt = Integer.parseInt(id.trim());
                insertLabelDataRelation(nextId, idInt);
            }
        } catch(Exception e) {
            logger.error("插入数据资产失败，资产信息：{}， 报错信息：{}", data.toString(), e.getMessage());
        }
    }


    public void createHBaseData() {}
    public void createHDFSData() {}




    public int insert(Data data) {
        return dataMapper.insert(data);
    }

    public int insertLabelDataRelation(long dataId, int labelId) {
        return dataMapper.insertLabelDataRelation(dataId, labelId);
    }

    public int insertDataSourceDataRelation(long dataId, long dataSourceId) {
        return dataMapper.insertDataSourceDataRelation(dataId, dataSourceId);
    }

    /**
     * 数据的删除
     */
    public int deleteById(int dataId) {
        return dataMapper.deleteById(dataId);
    }

    public int deleteDataSourceDataRelation(int dataId) {
        return dataMapper.deleteDataSourceDataRelation(dataId);
    }

    public int deleteLabelDataRelation(int dataId) {
        return dataMapper.deleteLabelDataRelation(dataId);
    }



    /**
     * 数据的修改
     */
    public int update(Data data) {
        return dataMapper.update(data);
    }

    /**
     * 数据的查询 - 高速检索接口
     */
    public List<Data> search(int creatorId, String name, int type, String labels, int pageNo, int pageSize, String startDate, String endDate) {
        int offset = 0;
        if (pageNo > 1) { offset = (pageNo - 1) * 10; }
        return dataMapper.search(creatorId, name, type, queryDataIdsByLabelId(labels), offset, pageSize, startDate, endDate);
    }

    public Integer searchTotal(int creatorId, String name, int type, String labels, String startDate, String endDate) {
        return dataMapper.searchTotal(creatorId, name, type, queryDataIdsByLabelId(labels), startDate, endDate);
    }

    /**
     * 记录用户的查询动作
     */
    public int log(int userId, String keyword) {
        return searchMapper.insert(userId, keyword);
    }

    /**
     * 数据的查询 - 数据源关联接口
     */
    public List<Data> list(int creatorId, int dataSourceId, int pageNo, int pageSize) {
        int offset = 0;
        if(pageNo != 1) {
            offset = (pageNo - 1) * pageSize;
        }
        return dataMapper.list(creatorId, dataSourceId, offset, pageSize);
    }

    public Integer listTotal(int creatorId, int dataSourceId) {
        return dataMapper.listTotal(creatorId, dataSourceId);
    }

    /**
     * 数据查询接口 - ID查询
     */
    public Data queryById(int dataId) {
        return dataMapper.queryById(dataId);
    }

    public DataSource queryDataSourceByDataId(int dataId) {
        return dataMapper.queryDataSourceByDataId(dataId);
    }

    public Long queryMaxId() {
        return dataMapper.queryMaxId();
    }
    /**
     * 数据详情
     * 针对不同的数据类型，返回的数据格式有所差异
     * HDFS：    返回HDFS文件的具体存储路径、大小、创建时间、权限等相关信息
     * Hive：    返回Hive表的字段信息，还可以添加字段
     * HBase:    返回HBase的字段族信息
     */
    public List<Map<String, String>> detail(int dataSourceId, int dataId) {
        Data data = dataMapper.queryById(dataId);
        DataSource dataSource = dataSourceMapper.queryById(dataSourceId);
        if(data.getType() == DbType.HIVE.getIndex()) {
            return HiveUtils.getTableColumnMapList(dataSource, data.getDataName());
        } else if(data.getType() == DbType.HBASE.getIndex()) {

        }
        return null;
    }

    /**
     * for analysis dashboard
     */
    public Result countDataByStatus() {
        Result resultO  = new Result();
        Map<String, Object>  result = new HashMap<>();
        List<Map<String, Object>> list = dataMapper.groupByStatus();
        long total = 0L;
        for(Map<String, Object> map : list) {
            String status = map.get("status").toString();
            if(status.equals("0")) {
                result.put("normal", Long.parseLong(map.get("total").toString()));
            } else {
                result.put("abnormal", Long.parseLong(map.get("total").toString()));
            }
            total += Long.parseLong(map.get("total").toString());
        }
        result.put("total", total);
        result.put("normalPercentage", com.bcht.data_manager.utils.StringUtils.getPercentage(Double.parseDouble(result.get("normal").toString()), Double.parseDouble(result.get("total").toString())));
        resultO.setData(result);
        putMsg(resultO, Status.SUCCESS);
        return resultO;
    }

    public Result countDownloadByDay() {
        Result result = new Result();
        Map<String, Object> dataMap = new HashMap<>();

        // 默认查询近10天的数据下载量
        String endDate = DateUtils.getDate() + " 23:59:59";
        String startDate = DateUtils.formatDate(DateUtils.nextDay(-10), DateUtils.DATE_FORMAT) + " 00:00:00";

        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> tmpList = dataMapper.countDownloadByDay(startDate, endDate);
        long total = 0L;
       for(Map<String, Object> map : tmpList) {
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("x", map.get("dayStr").toString());
            newMap.put("y", Long.parseLong(map.get("total").toString()));
            resultList.add(newMap);
            total += Long.parseLong(map.get("total").toString());
       }
       dataMap.put("total", total);
       dataMap.put("avgDay", total/10);
       result.setData(resultList);
       result.setDataMap(dataMap);
       putMsg(result, Status.SUCCESS);
       return result;
   }

    public Result countIncreaseByDay(String startDate, String endDate) {
        Result result  = new Result();
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> tmpList = dataMapper.countIncreaseByDay(startDate + " 00:00:00", endDate + " 23:59:59");
        for(Map<String, Object> map : tmpList) {
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("x", map.get("dayStr").toString());
            newMap.put("y", Long.parseLong(map.get("total").toString()));
            resultList.add(newMap);
        }
        result.setData(resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result countByLabel(String startDate, String endDate) {
        Result result  = new Result();
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> tmpList = dataMapper.countByLabel(startDate + " 00:00:00", endDate + " 23:59:59");
        for(Map<String, Object> map : tmpList) {
            Map<String, Object> newMap = new HashMap<>();
            newMap.put("name", map.get("name").toString());
            newMap.put("total", Long.parseLong(map.get("total").toString()));
            resultList.add(newMap);
        }
        result.setData(resultList);
        putMsg(result, Status.SUCCESS);
        return result;
    }



    private String queryDataIdsByLabelId(String labels) {
       String dataIds = null;
       if(!StringUtils.isEmpty(labels)) {
           Set<Integer> targetDataIdSet = new HashSet<>();
           String[] labelArray = labels.split(",");
           for(String label : labelArray) {
               if(StringUtils.isEmpty(label)){
                   continue;
               } else {
                   int labelId = Integer.parseInt(label);
                   List<Integer> ids = dataMapper.queryDataIdsByLabel(labelId);
                   targetDataIdSet.addAll(ids);
               }
           }
           dataIds = MapUtils.join(targetDataIdSet, ",");
       }
       return dataIds;
   }

    public List<Map<String, Object>> dateTypePercentage(String startDate, String endDate) {
        return dataMapper.dateTypePercentage(startDate, endDate);
    }
}
