package com.bcht.data_manager.service;

import com.bcht.data_manager.consts.Constants;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
    public int insert(DataSource dataSource) {
        return dataSourceMapper.insert(dataSource);
    }

    public int update(DataSource dataSource) {
        return dataSourceMapper.update(dataSource);
    }

    public int deleteById(int dataSourceId) {
        return dataSourceMapper.deleteById(dataSourceId);
    }

    public DataSource queryById(long dataSourceId) {
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
            Connection connection ;
            try {
                connection =  HiveUtils.getHiveConnection(ip, port, category1);
                if(connection != null) {
                    putMsg(result, Status.CUSTOM_SUCESSS, "Hive连接测试成功");
                } else {
                    putMsg(result, Status.HIVE_CONNECTION_TEST_FAILED);
                }
            } catch (SQLException e) {
                putMsg(result, Status.HIVE_CONNECTION_TEST_FAILED);
                logger.error("Hive连接测试失败\n" + e.getMessage());
            } catch (ClassNotFoundException e) {
                putMsg(result, Status.HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT);
                logger.error("Hive连接测试失败\n" + e.getMessage());
            }
            return result;
        } else if (type == DbType.HBASE.getIndex()) {
            Configuration configuration = HBaseConfiguration.create();
            configuration.set(Constants.HBASE_ZOOKEEPER_QUOEUM, ip);
            configuration.set(Constants.HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT, port + "");
            try{
                HBaseAdmin.available(configuration);
                putMsg(result, Status.CUSTOM_SUCESSS, "HBase连接测试成功！");
            }catch (Exception e) {
                putMsg(result, Status.HBASE_CONNECTION_TEST_FAILED);
                logger.error("HBase连接测试失败\n" + e.getMessage());
            }
            return result;
        } else if (type == DbType.HDFS.getIndex()) {
            try {
                boolean success = HDFSUtils.checkConnection(ip, port);
                if(success) {
                    putMsg(result, Status.HDFS_CONNECTION_TEST_SUCCESS);
                } else{
                    putMsg(result, Status.HDFS_CONNECTION_TEST_FAILED);
                    logger.error("HDFS连接测试失败");
                }
            } catch (IOException e) {
                putMsg(result, Status.HDFS_CONNECTION_TEST_FAILED);
                logger.error("HDFS连接测试失败\n" + e.getMessage());
                return result;
            }
        } else {
            putMsg(result, Status.UNKOWN_DATASOURCE_TYPE);
        }
        return result;
    }

    public List<Map<String, Integer>> statistic(int creatorId) {
        return dataSourceMapper.statistic(creatorId);
    }
}
