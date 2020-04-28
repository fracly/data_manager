package com.bcht.data_manager.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.mapper.SearchMapper;
import com.bcht.data_manager.utils.HttpClient;
import com.bcht.data_manager.utils.PropertyUtils;
import com.bcht.data_manager.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

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
            long free = Long.parseLong(map.get("CapacityRemaining").toString());

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

    public List<Map<String, Object>> dateTypePercentage(String startDate, String endDate) {
        return searchMapper.dateTypePercentage(startDate, endDate);
    }

}
