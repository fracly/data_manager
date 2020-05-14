package com.bcht.data_manager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.DataSourceMapper;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static com.bcht.data_manager.utils.StringUtils.*;

@Service
public class DataService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private SearchMapper searchMapper;

    /**
     * 创建Hive数据，即Hive表
     *      方式1:直接通过SQL的方式执行
     *      方式2: 拼接字段的方式，生成SQL再执行
     */
    public Result createHiveData(DataSource dataSource, User loginUser, Integer createWay, String createSql, String tableName, String columns, String name, String description, String labels) {
        Result result = new Result();
        if (createWay == Constants.CREATE_TABLE_METHOD_OF_CREATE_SQL){
            tableName = getTableName(createSql);
        } else if (createWay == Constants.CREATE_TABLE_METHOD_OF_COLUMN_COMPOSE){
            createSql = composeCreateSql(dataSource.getCategory1(), tableName, columns);
        }
        try{
            HiveUtils.createTable(dataSource, createSql);
        } catch (SQLException e) {
            putMsg(result, Status.HIVE_CREATE_TABLE_FAILED);
            logger.error("Hive创建表失败\n" + e.getMessage());
            return result;
        } catch (ClassNotFoundException e) {
            putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
            logger.error("Hive创建表失败\n" + e.getMessage());
            return result;
        }

        Long newID = store2Mysql(dataSource, loginUser, name, tableName, description, labels, 0);
        result.setData(newID);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建Hive表成功");
        return result;
    }

    /**
     * 创建Hbase表
     */
    public Result createHBaseData(DataSource dataSource, User loginUser, String tableName, String columns, String name, String description, String labels) {
        Result result = new Result();
        try{
            HBaseUtils.createTable(dataSource, tableName, columns);
        } catch (IOException e) {
            putMsg(result, Status.HBASE_CREATE_TABLE_FAILED);
            logger.error("删除Hbase表\n" + e.getMessage());
            return result;
        }

        Long newID = store2Mysql(dataSource, loginUser, name, tableName, description, labels, 0);
        result.setData(newID);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建HBase表成功");
        return result;
    }

    /**
     * 创建HDFS文件，并上传
     */
    public Result createHDFSData(DataSource dataSource, User loginUser, String fileName, String name, String description, String labels) {
        Result result = new Result();
        long newID = store2Mysql(dataSource, loginUser, name, fileName, description, labels, 0);
        result.setData(newID);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建HDFS文件成功");
        return result;
    }

    private Long store2Mysql(DataSource dataSource, User loginUser, String name, String tableName, String description, String labels, Integer status) {
        Long currentMaxId = queryMaxId();
        Long nextId = (currentMaxId == null ? 0: currentMaxId) + 1;
        Data data = new Data();
        data.setId(nextId);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        data.setCreatorId(loginUser.getId());
        data.setName(name);
        data.setStatus(status);
        data.setType(dataSource.getType());
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
        return nextId;
    }

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

    public Result addColumn(int dataId, String columns) {
        Result result = new Result();

        Data data = dataMapper.queryById(dataId);
        DataSource dataSource = dataMapper.queryDataSourceByDataId(dataId);

        if(data.getType() != DbType.HIVE.getIndex()) {
            putMsg(result, Status.CUSTOM_FAILED, "目前只支持Hive表进行字段的添加");
            return result;
        } else{
            List<Map> list = JSON.parseArray(columns, Map.class);
            try{
                HiveUtils.addTableColumn(dataSource, data.getDataName(), list);
            } catch (SQLException e) {
                putMsg(result, Status.HIVE_TABLE_ADD_COLUMN_FAILED);
                logger.error("Hive表添加字段失败！" + e.getMessage());
                return result;
            } catch (ClassNotFoundException e) {
                putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
                logger.error("Hive驱动类加载失败！" + e.getMessage());
                return result;
            }

            data.setUpdateTime(new Date());
            dataMapper.update(data);
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "添加字段成功");
        return result;
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
    public int logSearch(int userId, String keyword) {
        return searchMapper.insert(userId, keyword);
    }

    /**
     * 记录用户的下载动作
     */
    public int logDownload(int userId, Data data) {
        return dataMapper.insertDownload(userId, data);
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
    public Data queryById(long dataId) {
        return dataMapper.queryById(dataId);
    }

    public DataSource queryDataSourceByDataId(long dataId) {
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
    public Result hiveDetail(int dataId) {
        Result result = new Result();
        List<Map<String, String>> resultMapList;
        Data data = dataMapper.queryById(dataId);
        DataSource dataSource = dataMapper.queryDataSourceByDataId(dataId);
        try{
            resultMapList = HiveUtils.getTableColumnMapList(dataSource, data.getDataName());
            result.setData(resultMapList);
            putMsg(result, Status.CUSTOM_SUCESSS, "查询Hive数据详情成功");
        } catch (SQLException e) {
            putMsg(result, Status.HIVE_QUERY_HIVE_COLUMN_DETAIL_FAILED);
            logger.error("查询Hive数据表详情失败\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
            logger.error("查询Hive数据表详情失败\n" + e.getMessage());
        }
        return result;
    }

    public Result hbaseDetail(int dataId) {
        List<String> list ;
        Result result = new Result();

        Data data = dataMapper.queryById(dataId);
        DataSource dataSource = dataMapper.queryDataSourceByDataId(dataId);
        try{
            list = HBaseUtils.getTableColumnFamilyList(dataSource, data.getDataName());
            result.setData(list);
            putMsg(result, Status.CUSTOM_SUCESSS, "查询HBase数据详情成功");
        } catch (IOException e) {
            putMsg(result, Status.HBASE_QUERY_HBASE_COLUMN_FAMILY_DETAIL_FAILED);
            logger.error("查询HBase数据表详情失败\n" + e.getMessage());
        }
        return result;
    }

    public Result hdfsDetail(int dataId) {
        ContentSummary contentSummary;
        Result result = new Result();

        Data data = dataMapper.queryById(dataId);
        DataSource dataSource = dataMapper.queryDataSourceByDataId(dataId);
        try{
            contentSummary =  HDFSUtils.getFileInfo(dataSource, data.getDataName());
            result.setData(contentSummary);
            putMsg(result, Status.CUSTOM_SUCESSS, "查询HDFS数据详情成功");
        } catch (IOException e) {
            putMsg(result, Status.HDFS_QUERY_HDFS_FILE_DETAIL_FAILED);
            logger.error("查询HDFS文件详情失败\n" + e.getMessage());
        }
        return result;
    }

    public Result hdfsPreview(int dataId) {
        Result result = new Result();
        Data data = dataMapper.queryById(dataId);
        List<String> list;
        try{
            list = HDFSUtils.previewFile(data.getDataName());
            result.setData(list);
            putMsg(result, Status.SUCCESS);
        } catch (IOException e) {
            putMsg(result, Status.HDFS_PREVIEW_FILE_DATA_FAILED);
            logger.error("预览HDFS文件失败\n" + e.getMessage());
        }
        return result;
    }

    public Result tablePreview(int dataId) {
        Result result = new Result();
        DataSource dataSource = dataMapper.queryDataSourceByDataId(dataId);
        Data data = dataMapper.queryById(dataId);

        List<String> list;
        try{
            if(dataSource.getType() == DbType.HIVE.getIndex()) {
                list = HiveUtils.previewTableData(dataSource, data.getDataName());
            } else if(dataSource.getType() == DbType.HBASE.getIndex()) {
                list = HBaseUtils.previewTableData(dataSource, data.getDataName());
            } else{
                list = null;
            }
            result.setData(list);
            putMsg(result, Status.SUCCESS);
        } catch (SQLException e) {
            putMsg(result, Status.HIVE_PREVIEW_TABLE_DATA_FAILED);
            logger.error("预览Hive表数据失败\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
            logger.error("预览Hive表数据失败\n" + e.getMessage());
        } catch (IOException e) {
            putMsg(result, Status.HBASE_PREVIEW_TABLE_DATA_FAILED);
            logger.error("预览Hive表数据失败\n" + e.getMessage());
        }
        return result;
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
        result.setData(MapUtils.formatMapList(dataMapper.countIncreaseByDay(startDate + " 00:00:00", endDate + " 23:59:59"), "y"));
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
