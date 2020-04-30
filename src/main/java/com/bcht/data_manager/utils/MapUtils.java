package com.bcht.data_manager.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapUtils {
    public static final Logger logger = LoggerFactory.getLogger(MapUtils.class);

    public static final Integer getInt(Map<String, Object> map, String key) {
        int result = 0;
        Object valueObj = map.get(key);
        if(valueObj != null) {
            try{
                result  = Integer.parseInt(valueObj.toString());
            }catch(Exception e) {
                logger.error("调用工具类MapUtils.getInt出错：" + e.getMessage());
            }
        }
        return result;
    }

    public static final String getString(Map<String, Object> map, String key) {
        Object valueObj = map.get(key);
        if(valueObj != null) {
            return valueObj.toString();
        } else {
            return "";
        }
    }

    public static final MultipartFile getFile(Map<String, Object> map, String key) {
        Object obj = map.get(key);
        if(obj != null) {
            return (MultipartFile) obj;
        }
        return null;
    }

    public static final String join(Set<Integer> set, String joinChar) {
        StringBuilder sb = new StringBuilder();
        for(Integer integer : set) {
            sb.append(integer +  joinChar);
        }
        String resultStr = sb.toString();
        return resultStr.substring(0, resultStr.length() - joinChar.length());
    }
}
