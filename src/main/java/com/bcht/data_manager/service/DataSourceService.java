package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.mapper.DataSourceMapper;
import com.bcht.data_manager.utils.HiveUtils;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);

    @Autowired
    private DataSourceMapper dataSourceMapper;

    public int insert(DataSource dataSource) {
        HiveUtils.createDatabase(dataSource);
        return dataSourceMapper.insert(dataSource);
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

        } else if (type == DbType.HDFS.getIndex()) {

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
