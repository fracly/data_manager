package com.bcht.data_manager.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * datasource mapper provider
 */
public class DataSourceMapperProvider {

    private static final String DATASOURCE_TABLE_NAME = "t_data_manager_datasource";


    /**
     * insert datasource
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATASOURCE_TABLE_NAME);
                VALUES("`name`", "#{dataSource.name}");
                VALUES("`type`", "#{dataSource.type}");
                VALUES("`ip`", "#{dataSource.ip}");
                VALUES("`port`", "#{dataSource.port}");
                VALUES("`category1`", "#{dataSource.category1}");
                VALUES("`description`", "#{dataSource.description}");
                VALUES("`creatorId`", "#{dataSource.creatorId}");
            }
        }.toString();
    }

    /**
     * delete datasource
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(DATASOURCE_TABLE_NAME);
                WHERE("`id`=#{dataSourceId}");
            }
        }.toString();
    }

    /**
     * update datasource
     */
    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(DATASOURCE_TABLE_NAME);
                SET("`name` = #{dataSource.name}");
                SET("`type` = #{dataSource.type}");
                SET("`ip` = #{dataSource.ip}");
                SET("`port` = #{dataSource.port}");
                SET("`category1` = #{dataSource.category1}");
                SET("`description` = #{dataSource.description}");
                WHERE("`id` = #{dataSource.id}");
            }
        }.toString();
    }

    /**
     * query by datasource id
     */
    public String queryById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(DATASOURCE_TABLE_NAME);
                WHERE("`id` = #{dataSourceId}");
            }
        }.toString();
    }

    /**
     * query datasource list by user id
     */
    public String queryByUserId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("id, name, type, ip, port, category1, description, creatorId");
                FROM(DATASOURCE_TABLE_NAME);
                WHERE("`creatorId` = #{userId}");
            }
        }.toString();
    }

    /**
     * statistic
     */
    public String statistic(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("type, count(1) as total");
            FROM(DATASOURCE_TABLE_NAME);
            GROUP_BY("type");
            WHERE("creatorId = ${creatorId}");
        }}.toString();
    }
}
