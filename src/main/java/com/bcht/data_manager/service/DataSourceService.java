package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.DataSourceMapper;
import com.bcht.data_manager.utils.HBaseUtils;
import com.bcht.data_manager.utils.HDFSUtils;
import com.bcht.data_manager.utils.HiveUtils;
import com.bcht.data_manager.utils.Result;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);

    @Autowired
    private DataSourceMapper dataSourceMapper;

    /**
     * 创建数据源说明：
     *      Hive  => Database  划分业务
     *      HDFS  => Directory 划分业务
     *      HBase => NameSpace 划分业务
     */
    public Result insert(DataSource dataSource) {
        Result result = new Result();
        if(dataSource.getType() == DbType.HIVE.getIndex()) {
            HiveUtils.createDatabase(dataSource);
        } else if(dataSource.getType() == DbType.HBASE.getIndex()) {
            HBaseUtils.createNameSpace(dataSource);
        } else if(dataSource.getType() == DbType.HDFS.getIndex()) {
            boolean success = HDFSUtils.mkdir(dataSource, dataSource.getCategory1());
            if(!success) {
                putMsg(result, Status.CUSTOM_FAILED, "HDFS目录存在且不为空");
                return result;
            }
        }
        int count = dataSourceMapper.insert(dataSource);
        if(count > 0) {
            putMsg(result, Status.CUSTOM_SUCESSS, "创建数据源成功");
        } else {
            putMsg(result, Status.CUSTOM_FAILED, "创建数据源失败");
        }
        return result;
    }

    public int update(DataSource dataSource) {
        return dataSourceMapper.update(dataSource);
    }

    public int deleteById(int dataSourceId) {
        return dataSourceMapper.deleteById(dataSourceId);
    }

    public DataSource queryById(int dataSourceId) {
        return dataSourceMapper.queryById(dataSourceId);
    }

    public Map<String, Long> groupByType() {
        Map<String, Long>  result = new HashMap<>();
        List<Map<String, Object>> list = dataSourceMapper.groupByType();
        long totalCount = 0L;
        for(Map<String, Object> map : list) {
            if (map.get("type").toString().equals("1")) {
                result.put(DbType.HIVE.getName(), Long.parseLong(map.get("total").toString()));
            } else if (map.get("type").toString().equals("2")) {
                result.put(DbType.HBASE.getName(), Long.parseLong(map.get("total").toString()));
            } else {
                result.put(DbType.HDFS.getName(), Long.parseLong(map.get("total").toString()));
            }
            totalCount += Long.parseLong(map.get("total").toString());
        }
        result.put("total", totalCount);
        return result;
    }

    public List<DataSource> query(int userId, int type, String name) {
        return dataSourceMapper.query(userId, type, name);
    }

    public Result testConnection(int type, String ip, int port, String category1) {
        Result result = new Result();
        if (type == DbType.HIVE.getIndex()) {
            Connection connection = HiveUtils.getHiveConnection(ip, port, category1);
            if(connection != null) {
                result.setMsg("Hive连接测试成功");
                result.setCode(0);
            } else {
                result.setMsg("Hive连接测试失败");
                result.setCode(-1);
            }
        } else if (type == DbType.HBASE.getIndex()) {
            Configuration configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", ip);
            configuration.set("hbase.zookeeper.property.clientPort", port + "");
            try{
                HBaseAdmin.available(configuration);
                result.setMsg("HBase连接成功");
                result.setCode(0);
            }catch (Exception e) {
                result.setMsg("HBase连接失败");
                result.setCode(-1);
                e.printStackTrace();
            }
        } else if (type == DbType.HDFS.getIndex()) {
            boolean success = HDFSUtils.checkConnection(ip, port);
            if(success) {
                result.setCode(0);
                result.setMsg("HDFS连接成功");
            } else {
                result.setCode(-1);
                result.setMsg("HDFS连接失败");
            }
        } else {
            result.setCode(-1);
            result.setMsg("未知的数据源类型");
        }
        return result;
    }

    public List<Map<String, Integer>> statistic(int creatorId) {
        return dataSourceMapper.statistic(creatorId);
    }
}
