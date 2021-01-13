package com.bcht.data_manager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.mapper.DataMapper;
import com.bcht.data_manager.mapper.LogMapper;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.mapper.SystemMapper;
import com.bcht.data_manager.utils.HttpClient;
import com.bcht.data_manager.utils.MapUtils;
import com.bcht.data_manager.utils.PropertyUtils;
import com.bcht.data_manager.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    @Autowired
    private HttpClient client;

    @Autowired
    private SearchMapper searchMapper;

    @Autowired
    private DataMapper dataMapper;

    @Autowired
    private LogMapper logMapper;

    public Map<String, String> capacity() {
        Map<String, String> result = new HashMap<>();
        String json = client.request(PropertyUtils.getString(Constants.CLOUDEAR_HDFS_NAMENODE_URL), HttpMethod.GET, null);
        JSONObject object = JSON.parseObject(json);
        Object obj = object.get("beans");
        JSONArray array = JSON.parseArray(obj.toString());
        if(array.size() >= 1) {
            Map<String, Object> map = (Map<String, Object>)array.get(0);

            // 默认的大小单位为byte，需要进行大小转化
            long total = Long.parseLong(map.get("CapacityTotal").toString());
            long used = Long.parseLong(map.get("CapacityUsed").toString());
            long free =  Long.parseLong(map.get("CapacityRemaining").toString());

            result.put("CapacityTotal", StringUtils.byteFormat(total));
            result.put("CapacityUsed", StringUtils.byteFormat(used));
            result.put("CapacityRemaining", StringUtils.byteFormat(free));
            result.put("CapacityUsedPercentage", StringUtils.getPercentage((double)used, (double)total));
        }
        return result;
    }

    public List<Map<String, Object>> searchKeyword(String startDate, String endDate) {
        List<Map<String, Object>> resultList = searchMapper.searchKeyword(startDate, endDate);
        for(int i = 1; i <= resultList.size(); i ++ ){
            Map<String, Object> tmp = resultList.get(i - 1);
            tmp.put("index", i);
            tmp.put("userCount", 1);
        }
        return resultList;
    }

    public List<Map<String, Object>> userActive(String startDate, String endDate) {
        List<Map<String, Object>> userActiveList = searchMapper.userActive(startDate, endDate);
        return MapUtils.formatMapList(userActiveList, "loginY", "searchY", "downloadY");
    }

    public List<Map<String, Object>> searchCountByDay(String startDate, String endDate) {
        List<Map<String, Object>> tmpList = searchMapper.searchCountByDay(startDate, endDate);
        return MapUtils.formatMapList(tmpList, "y");
    }

    public List<Map<String, Object>> searchUserByDay(String startDate, String endDate) {
        List<Map<String, Object>> tmpList = searchMapper.searchUserByDay(startDate, endDate);
        return MapUtils.formatMapList(tmpList, "y");
    }

    public List<Map<String, Object>> loginCountByDay(String startDate, String endDate) {
        List<Map<String, Object>> tmpList = searchMapper.loginCountByDay(startDate, endDate);
        return MapUtils.formatMapList(tmpList, "y");
    }

    public Integer searchUserTotal(String startDate, String endDate) {
        return searchMapper.searchUserTotal(startDate, endDate);
    }

    public List<Map<String, Object>> report(String startDate, String endDate) {
        List<Map<String, Object>> increaseByDay = dataMapper.countIncreaseByDay(startDate, endDate);
        List<Map<String, Object>> loginByDay = logMapper.countLoginByDay(startDate, endDate);
        List<Map<String, Object>> downloadByDay = logMapper.countDownloadByDay(startDate, endDate);
        List<Map<String, Object>> searchByDay = logMapper.countSearchByDay(startDate, endDate);

        List<Map<String, Object>> resultList = new ArrayList<>();
        for(int i = 0; i < loginByDay.size(); i ++) {
            Map<String, Object> first = new HashMap<>();
            String dayStr = loginByDay.get(i).get("dayStr").toString();
            first.put("day", dayStr);
            first.put("login", loginByDay.get(i).get("total"));
            boolean searchFound = false;
            for(int j = 0; j < searchByDay.size(); j ++) {
                if (searchByDay.get(j) != null && searchByDay.get(j).get("dayStr").equals(dayStr)) {
                    first.put("search", searchByDay.get(j).get("total"));
                    searchFound = true;
                }
            }
            if(!searchFound) {
                first.put("search", 0);
            }

            boolean downloadFound = false;
            for(int j = 0; j < downloadByDay.size(); j ++) {
                if (downloadByDay.get(j).get("dayStr").equals(dayStr)) {
                    first.put("download", downloadByDay.get(j).get("total"));
                    downloadFound = true;
                }
            }
            if(!downloadFound) {
                first.put("download", 0);
            }

            boolean increaseFound = false;
            for(int j = 0; j < increaseByDay.size(); j ++) {
                if (increaseByDay.get(j).get("dayStr").equals(dayStr)) {
                    first.put("increase", increaseByDay.get(j).get("total"));
                    increaseFound = true;
                }
            }
            if(!increaseFound) {
                first.put("increase", 0);
            }
            resultList.add(first);
        }
        return resultList;
    }

}
