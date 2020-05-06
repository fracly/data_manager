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
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 999) {
                    WHERE(" status = #{status}" );
                }
                ORDER_BY("start_time desc");
            }
        }.toString();
    }

    public String jobDelete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(IMPORT_LOG_TABLE_NAME);
                WHERE("`id`=#{id}");
            }
        }.toString();
    }

    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(IMPORT_LOG_TABLE_NAME);
                VALUES("`type`", "#{job.type}");
                VALUES("`input_type`", "#{job.inputType}");
                VALUES("`input_parameter`", "#{job.inputParameter}");
                VALUES("`output_type`", "#{job.outputType}");
                VALUES("`output_id`", "#{job.outputId}");
                VALUES("`start_time`", "#{job.startTime}");
                VALUES("`end_time`", "#{job.endTime}");
                VALUES("`status`", "#{job.status}");
                VALUES("`creatorId`", "#{job.creatorId}");
            }
        }.toString();
    }
}
