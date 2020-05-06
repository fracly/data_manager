package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class CollectionMapperProvider {

    private static final String IMPORT_LOG_TABLE_NAME = "t_data_manager_import_log";

    public String jobList(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(IMPORT_LOG_TABLE_NAME);
                WHERE("creatorId = #{creatorId}");
                Object nameObj = parameter.get("name");
                if(nameObj != null && StringUtils.isNotEmpty(nameObj.toString())) {
                    WHERE("name like concat('%', #{name}, '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status = #{status}" );
                }
                ORDER_BY("start_time");
            }
        }.toString();
    }
}
