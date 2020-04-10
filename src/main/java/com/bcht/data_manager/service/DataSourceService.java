package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.mapper.DataSourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSourceService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);

    @Autowired
    private DataSourceMapper dataSourceMapper;

    public int insert(DataSource dataSource) {
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

    public List<DataSource> queryByUserId(int userId) {
        return dataSourceMapper.queryByUserId(userId);
    }
}
