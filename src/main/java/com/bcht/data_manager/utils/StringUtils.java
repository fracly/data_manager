package com.bcht.data_manager.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

/**
 * 业务强相关的字符串工具类
 */
public class StringUtils {

    /**
     * 从建表语句中获取表名
     */
    public static final String getTableName(String createSql) {
        if (createSql != null) {
            String[] firstArray = createSql.trim().replace(";", "").split("\\(");
            String[] secondArray = firstArray[0].split(" ");
            return secondArray[secondArray.length - 1];
        }
        return null;
    }

    /**
     * 用表名、库名、字段信息拼成建表语句
     */
    public static final String composeCreateSql(String database, String tableName, String columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table " + database + "." + tableName + " (");
        List<Map> list = JSON.parseArray(columns, Map.class);
        for (int i = 0; i < list.size(); i ++) {
            Map<String, String> map = list.get(i);
            sb.append(map.get("name") + " " + map.get("type") + " " + map.get("comment"));
        }
        sb.append(")");
        return sb.toString();
    }
}
