package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class SearchMapperProvider {

    private static final String DATA_TABLE_NAME = "t_data_manager_data";

    public static final String SEARCH_LOG_TABLE_NAME = "t_data_manager_search_log";


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


    /**
     * query by data id
     */
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
}