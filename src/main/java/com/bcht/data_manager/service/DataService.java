package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.DataSourceMapper;
import com.bcht.data_manager.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DataService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private DataMapper dataMapper;

    public int insert(Data data) {
        return dataMapper.insert(data);
    }

    public int insertDataSourceDataRelation(int dataId, int dataSourceId) {
        return dataMapper.insertDataSourceDataRelation(dataId, dataSourceId);
    }

    public int insertLabelDataRelation(int dataId, int labelId) {
        return dataMapper.insertLabelDataRelation(dataId, labelId);
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

    public List<Data> search(int creatorId, String name, int type, String labels, int pageNo, int pageSize, String startDate, String endDate) {
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
        int offset = 0;
        if (pageNo > 1) {
            offset = (pageNo - 1) * 10;
        }
        return dataMapper.search(creatorId, name, type, dataIds, offset, pageSize, startDate, endDate);
    }

    public Integer searchTotal(int creatorId, String name, int type, String labels, String startDate, String endDate) {
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
        return dataMapper.searchTotal(creatorId, name, type, dataIds, startDate, endDate);
    }

    public List<Data> queryByName(String name) {
        return dataMapper.queryByName(name);
    }

    public List<Data> listByDataSource(int creatorId, int dataSourceId, int pageNo, int pageSize) {
        int offset = 0;
        if(pageNo != 1) {
            offset = (pageNo - 1) * pageSize;
        }
        return dataMapper.listByDataSource(creatorId, dataSourceId, offset, pageSize);
    }

    public Integer listByDataSourceTotal(int creatorId, int dataSourceId) {
        return dataMapper.listByDataSourceTotal(creatorId, dataSourceId);
    }

    public List<Data> listByUser(int creatorId, int pageNo, int pageSize) {
        int offset = 0;
        if(pageNo != 1) {
            offset = (pageNo - 1) * pageSize;
        }
        return dataMapper.listByUser(creatorId, offset, pageSize);
    }

    public Integer listByUserTotal(int creatorId) {
        return dataMapper.listByUserTotal(creatorId);
    }

    public Integer queryMaxId(){
        return dataMapper.queryMaxId();
    }
}
