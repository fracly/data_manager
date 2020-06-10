package com.bcht.data_manager.service;

import com.alibaba.fastjson.JSON;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.*;
import com.bcht.data_manager.enums.DbType;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.LogMapper;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.ContentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static com.bcht.data_manager.utils.StringUtils.composeCreateSql;
import static com.bcht.data_manager.utils.StringUtils.getTableName;

@Service
public class LogService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(LogService.class);

    @Autowired
    private LogMapper logMapper;

    public Result loginLog(String name, String startTime, String endTime, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        int offset = (pageNo - 1) * pageSize;
        List<LoginRecord> list = logMapper.loginLog(name, startTime, endTime, offset, pageSize);
        int total = logMapper.countSearchLog(name, startTime, endTime);
        Map map = new HashMap();
        map.put("total", total);
        result.setData(list);
        result.setDataMap(map);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result downloadLog(String name, String startTime, String endTime, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        int offset = (pageNo - 1) * pageSize;
        List<DownloadRecord> list = logMapper.downloadLog(name, startTime, endTime, offset, pageSize);
        int total = logMapper.countDownloadLog(name, startTime, endTime);
        Map map = new HashMap();
        map.put("total", total);
        result.setData(list);
        result.setDataMap(map);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result searchLog(String name, String startTime, String endTime, Integer pageNo, Integer pageSize) {
        Result result = new Result();
        int offset = (pageNo - 1) * pageSize;
        List<SearchRecord> list = logMapper.searchLog(name, startTime, endTime, offset, pageSize);
        int total = logMapper.countSearchLog(name, startTime, endTime);
        Map map = new HashMap();
        map.put("total", total);
        result.setData(list);
        result.setDataMap(map);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }
}
