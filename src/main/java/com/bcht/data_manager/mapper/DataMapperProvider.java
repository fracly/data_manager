package com.bcht.data_manager.mapper;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class DataMapperProvider {

    private static final String DATA_TABLE_NAME = "t_data_manager_data";

    private static final String DATASOURCE_DATA_RELATION_TABLE_NAME = "t_data_manager_relation_datasource_data";

    /**
     * insert data
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATA_TABLE_NAME);
                VALUES("`id`", "#{data.id");
                VALUES("`name`", "#{data.name}");
                VALUES("`type`", "#{data.type}");
                VALUES("`data_name`", "#{data.dataName}");
                VALUES("`size`", "#{data.size}");
                VALUES("`creatorId`", "#{data.creatorId}");
                VALUES("`create_time`", "#{data.createTime}");
                VALUES("`description`", "#{data.description}");
                VALUES("`update_time`", "#{data.updateTime}");
                VALUES("`status`", "#{data.status}");
                VALUES("`destroy_method`", "#{data.destroyMethod}");
                VALUES("`destroy_time`", "#{data.destroyTime}");
            }
        }.toString();
    }

    public String insertRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATASOURCE_DATA_RELATION_TABLE_NAME);
                VALUES("`data_id`", "#{dataId}");
                VALUES("`datasource_id`", "#{dataSourceId}");
            }
        }.toString();
    }

    /**
     * delete data
     */
    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(DATA_TABLE_NAME);
                WHERE("`id`=#{dataId}");
            }
        }.toString();
    }

    /**
     * update data
     */
    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(DATA_TABLE_NAME);
                SET("`name` = #{data.name}");
                SET("`type` = #{data.type}");
                SET("`ip` = #{data.ip}");
                SET("`port` = #{data.port}");
                SET("`category1` = #{data.category1}");
                SET("`description` = #{data.description}");
                WHERE("`id` = #{data.id}");
            }
        }.toString();
    }

    /**
     * query by data id
     */
    public String queryById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(DATA_TABLE_NAME);
                WHERE("`id` = #{dataId}");
            }
        }.toString();
    }

    /**
     * query by name
     */
    public String queryByName(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(DATA_TABLE_NAME);
                WHERE("name like concat('%', #{name}, '%')");
            }
        }.toString();
    }

    /**
     * query data list by user id
     */
    public String queryByUserId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("id, name, type, ip, port, category1, description, creatorId");
                FROM(DATA_TABLE_NAME);
                WHERE("`creatorId` = #{userId}");

            }
        }.toString();
    }

    public String listByDataSource(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*");
                FROM(DATA_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id ");
                WHERE(" b.datasource_id = #{dataSourceId} ");
                ORDER_BY(" a.id desc limit #{offset}, #{pageSize}");
            }
        }.toString();
    }

    public String countByDataSource(Map<String, Object> parameter) {
        return new SQL(){{
            SELECT("count(1)");
            FROM(DATA_TABLE_NAME + " a ");
            INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id ");
            WHERE(" b.datasource_id = #{dataSourceId} ");
        }}.toString();
    }

    public String queryMaxId(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("max(id)");
            FROM(DATA_TABLE_NAME);
        }}.toString();
    }
}