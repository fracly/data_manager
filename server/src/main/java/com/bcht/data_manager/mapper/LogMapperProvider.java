package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class LogMapperProvider {

    private static final String LOGIN_LOG_TABLE_NAME = "t_data_manager_login_log";

    private static final String DOWNLOAD_LOG_TABLE_NAME = "t_data_manager_download_log";

    private static final String SEARCH_LOG_TABLE_NAME = "t_data_manager_search_log";

    public static final String USER_TABLE_NAME = "t_data_manager_user";

    public String loginLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(LOGIN_LOG_TABLE_NAME);
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE("login_time >= '#{startTime}' and login_time <= '#{endTime}'");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                ORDER_BY("login_time desc limit " + parameter.get("offset").toString() + ", " + parameter.get("pageSize"));
            }
        }.toString();
    }

    public String countLoginLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(LOGIN_LOG_TABLE_NAME);
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE("login_time >= '#{startTime}' and login_time <= '#{endTime}'");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
            }
        }.toString();
    }

    public String searchLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.id, a.search_time, a.keyword, b.name, b.username");
                FROM(SEARCH_LOG_TABLE_NAME + " a");
                INNER_JOIN(USER_TABLE_NAME + " b on a.user_id = b.id");
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE(" a.search_time >= '" + startTime.toString() + "' and a.search_time <= '" + endTime.toString() + "'");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("b.name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                ORDER_BY("a.search_time desc limit " + parameter.get("offset").toString() + ", " + parameter.get("pageSize"));
            }
        }.toString();
    }

    public String countSearchLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(SEARCH_LOG_TABLE_NAME + " a");
                INNER_JOIN(USER_TABLE_NAME + " b on a.user_id = b.id");
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE(" a.search_time >= '" + startTime.toString() + "' and a.search_time <= '" + endTime.toString() + "'");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("b.name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
            }
        }.toString();
    }

    public String downloadLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.data_name, a.status, a.start_time, b.name, b.username");
                FROM(DOWNLOAD_LOG_TABLE_NAME + " a");
                INNER_JOIN(USER_TABLE_NAME + " b on a.user_id = b.id");
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE("`start_time` >= #{startTime} and `start_time` <= #{endTime}");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("a.data_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                ORDER_BY("a.start_time desc limit " + parameter.get("offset").toString() + ", " + parameter.get("pageSize"));
            }
        }.toString();
    }

    public String countDownloadLog(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(DOWNLOAD_LOG_TABLE_NAME + " a");
                INNER_JOIN(USER_TABLE_NAME + " b on a.user_id = b.id");
                Object startTime = parameter.get("startTime");
                Object endTime = parameter.get("endTime");
                if(startTime != null && endTime != null && StringUtils.isNotEmpty(startTime.toString()) && StringUtils.isNotEmpty(endTime.toString())) {
                    WHERE("`start_time` >= #{startTime} and `start_time` <= #{endTime}");
                }
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("a.data_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
            }
        }.toString();
    }


}
