package com.bcht.data_manager.utils;

import com.alibaba.fastjson.JSON;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
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
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> map = list.get(i);
            sb.append(map.get("name") + " " + map.get("type") + " " + map.get("comment"));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * 对空间大小进行格式化
     */
    public static String byteFormat(long bytes) {
        String[] units = new String[]{" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB"};
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        double pre = 0;
        if (bytes > 1024) {
            pre = bytes / Math.pow(unit, exp);
        } else {
            pre = (double) bytes / (double) unit;
        }
        return String.format(Locale.ENGLISH, "%.2f%s", pre, units[(int) exp]);
    }

    /**
     * 除数 和 被除数 进行百分比输出
     */
    public static String getPercentage(Double divisor, Double dividend) {
        if (dividend == null || divisor == null)
            return null;
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format(divisor / dividend * 100) + "%";
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getPercentage((double)1,(double)3));
    }
}
