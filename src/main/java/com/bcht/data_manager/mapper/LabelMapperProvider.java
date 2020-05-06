/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * label mapper provider
 */
public class LabelMapperProvider {

    private static final String LABEL_TABLE_NAME = "t_data_manager_label";

    private static final String RELATION_LABEL_DATA_TABLE_NAME = "t_data_manager_relation_label_data";

    public String list(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(LABEL_TABLE_NAME );
                WHERE("creatorId=#{creatorId}");
                Object searchVal = parameter.get("searchVal");
                if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())){
                    WHERE( " name like concat('%', #{searchVal}, '%') ");
                }
            }
        }.toString();
    }

    public String insert(Map<String, Object> parameter) {
        return new SQL(){{
            INSERT_INTO(LABEL_TABLE_NAME);
            VALUES("`name`", "#{label.name}");
            VALUES("`creatorId`", "#{label.creatorId}");
            VALUES("`create_time`", "#{label.createTime}");
        }}.toString();
    }

    public String delete(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(LABEL_TABLE_NAME);
                WHERE("`id`=#{labelId}");
            }
        }.toString();
    }

    public String update(Map<String, Object> parameter) {
        return new SQL() {
            {
                UPDATE(LABEL_TABLE_NAME);
                SET("`name` = #{label.name}");
                WHERE("`id` = #{label.id}");
            }
        }.toString();
    }

    public String queryById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(LABEL_TABLE_NAME);
                WHERE("id = #{labelId}");
            }
        }.toString();
    }

    public String queryByDataId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*");
                FROM(LABEL_TABLE_NAME + " a");
                INNER_JOIN(RELATION_LABEL_DATA_TABLE_NAME + " b on a.id = b.label_id ");
                WHERE("b.data_id = #{dataId}");
            }
        }.toString();
    }

    public String queryDataCountById(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(RELATION_LABEL_DATA_TABLE_NAME);
                WHERE("label_id=#{labelId}");
            }
        }.toString();
    }

    public String top10(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("*");
                FROM(LABEL_TABLE_NAME + " a ");
                WHERE("a.creatorId= #{creatorId}");
                INNER_JOIN("(select label_id, count(data_id) as total from t_data_manager_relation_label_data group by label_id) b on a.id=b.label_id ");
                ORDER_BY(" b.total desc limit 10");

            }
        }.toString();
    }

    public String countData(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(RELATION_LABEL_DATA_TABLE_NAME);
                WHERE("label_id = #{labelId}");
            }
        }.toString();
    }


}
