package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Label;
import com.bcht.data_manager.mapper.LabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LabelService extends BaseService {
    @Autowired
    private LabelMapper labelMapper;

    public List<Label> list(String searchVal) {
        List<Label>  labelList = labelMapper.list(searchVal);
        for(Label label : labelList) {
            label.setTotal(labelMapper.countData(label.getId()));
        }
        return labelList;
    }

    public boolean create(int loginUserId, String name) {
        Label label = new Label();
        label.setName(name);
        label.setCreatorId(loginUserId);
        label.setCreateTime(new Date());
        int count = labelMapper.insert(label);
        if( count > 0) {
            return true;
        }
        return false;
    }

    public boolean delete(int labelId) {
        int count = labelMapper.delete(labelId);
        if (count > 0) {
            return true;
        }
        return false;
    }

    public boolean deleteDataLabelRelation(int labelId, long dataId) {
        int count = labelMapper.deleteLabelDataRelation(labelId, dataId);
        if(count > 0) {
            return true;
        }
        return false;
    }

    public boolean insertDataLabelRelation(int labelId, long dataId) {
        int count = labelMapper.insertLabelDataRelation(labelId, dataId);
        if(count > 0) {
            return true;
        }
        return false;
    }

    public boolean update(int labelId, String name) {
        Label label = labelMapper.queryById(labelId);
        label.setName(name);
        int count  = labelMapper.update(label);
        if(count > 0) {
            return true;
        }
        return false;
    }

    public Label queryById(int labelId) {
        return labelMapper.queryById(labelId);
    }

    public int queryDataCountById(int labelId) {
        return labelMapper.queryDataCountById(labelId);
    }
    public List<Label> queryByDataId(long dataId) {
        return labelMapper.queryByDataId(dataId);
    }
}
