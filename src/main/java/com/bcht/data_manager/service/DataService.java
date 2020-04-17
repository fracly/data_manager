package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.DataSourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private DataMapper dataMapper;

    public int insert(Data data) {
        return dataMapper.insert(data);
    }

    public int insertRelation(int dataId, int dataSourceId) {
        return dataMapper.insertRelation(dataId, dataSourceId);
    }

    public int update(Data data) {
        return dataMapper.update(data);
    }

    public int deleteById(int dataId) {
        return dataMapper.deleteById(dataId);
    }

    public Data queryById(int dataId) {
        return dataMapper.queryById(dataId);
    }

    public List<Data> queryByName(String name) {
        return dataMapper.queryByName(name);
    }

    public List<Data> queryByUserId(int userId) {
        return dataMapper.queryByUserId(userId);
    }

    public List<Data> listByDataSource(int dataSourceId, int pageNo, int pageSize) {
        int offset = 0;
        if(pageNo != 1) {
            offset = (pageNo - 1) * pageSize;
        }
        return dataMapper.listByDataSource(dataSourceId, offset, pageSize);
    }

    public int countByDataSource(int dataSourceId) {
        return dataMapper.countByDataSource(dataSourceId);
    }

    public int queryMaxId(){
        return dataMapper.queryMaxId();
    }
}
