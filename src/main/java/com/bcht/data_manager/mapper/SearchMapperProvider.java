package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class SearchMapperProvider {

    public static final String SEARCH_LOG_TABLE_NAME = "t_data_manager_search_log";

    public static final String LOGIN_LOG_TABLE_NAME = "t_data_manager_login_log";

    public static final String DOWNLOAD_LOG_TABLE_NAME = "t_data_manager_download_log";


    /**
     * insert data
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(SEARCH_LOG_TABLE_NAME);
                VALUES("`keyword`", "#{name}");
                VALUES("`user_id`", "#{userId}");
                VALUES("`search_time`", "now()");
            }
        }.toString();
    }

    public String userActive(Map<String, Object> parameter) {
        String startDate = parameter.get("startDate").toString();
        String endDate = parameter.get("endDate").toString();
        String sql = "select a.dayStr, count(a.id) as loginCount, count(b.id) as searchCount, count(c.id) as downloadCount from " +
                "(select id, DATE_FORMAT(login_time,'%Y-%m-%d') as dayStr from t_data_manager_login_log where login_time>= '" + startDate + "' and login_time <= '" + endDate + "') a " +
                "left join " +
                "(select id, DATE_FORMAT(search_time,'%Y-%m-%d') as dayStr from t_data_manager_search_log where search_time>= '" + startDate + "' and search_time <= '" + endDate + "') b  on a.dayStr=b.dayStr " +
                "left join " +
                "(select id, DATE_FORMAT(start_time,'%Y-%m-%d') as dayStr from t_data_manager_download_log where start_time>= '" + startDate + "' and start_time <= '" + endDate + "') c on b.dayStr=c.dayStr " +
                "group by a.dayStr";

        return sql;
    }

    public String searchKeyword(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("keyword, count(1) as  count");
                FROM(SEARCH_LOG_TABLE_NAME);
                WHERE("`search_time` >= #{startDate}", "`search_time` <= #{endDate}");
                GROUP_BY("keyword");
                ORDER_BY("count(1) desc ");
            }
        }.toString();
    }

    public String searchCountByDay(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("DATE_FORMAT(search_time,'%Y-%m-%d') as dayStr, count(1) as total");
                FROM(SEARCH_LOG_TABLE_NAME);
                WHERE("`search_time` >= #{startDate}", "`search_time` <= #{endDate}");
                GROUP_BY("DATE_FORMAT(search_time,'%Y-%m-%d')");
                ORDER_BY("DATE_FORMAT(search_time,'%Y-%m-%d')");
            }
        }.toString();
    }

    public String loginCountByDay(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("DATE_FORMAT(login_time,'%Y-%m-%d') as dayStr, count(1) as total");
                FROM(LOGIN_LOG_TABLE_NAME);
                WHERE("`login_time` >= #{startDate}", "`login_time` <= #{endDate}");
                GROUP_BY("DATE_FORMAT(login_time,'%Y-%m-%d')");
                ORDER_BY("DATE_FORMAT(login_time,'%Y-%m-%d')");
            }
        }.toString();
    }

    public String searchUserByDay(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("DATE_FORMAT(search_time,'%Y-%m-%d') as dayStr, count(distinct user_id) as total");
                FROM(SEARCH_LOG_TABLE_NAME);
                WHERE("`search_time` >= #{startDate}", "`search_time` <= #{endDate}");
                GROUP_BY("DATE_FORMAT(search_time,'%Y-%m-%d')");
                ORDER_BY("DATE_FORMAT(search_time,'%Y-%m-%d')");
            }
        }.toString();
    }

    public String searchUserTotal(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(distinct user_id) as total");
                FROM(SEARCH_LOG_TABLE_NAME);
                WHERE("`search_time` >= #{startDate}", "`search_time` <= #{endDate}");
            }
        }.toString();
    }
}
