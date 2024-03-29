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

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * session mapper provider
 */
public class UserRoleMapperProvider {

    private static final String USER_TABLE_NAME = "t_data_manager_user";

    private static final String ROLE_TABLE_NAME = "t_data_manager_role";

    private static final String USER_ROLE_RELATION_TABLE_NAME = "t_data_manager_relation_user_role";

    /**
     * query user's all role
     *
     * @param parameter
     * @return
     */
    public String findRolesByUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("c.*");
                FROM(USER_TABLE_NAME + " a ");
                INNER_JOIN(USER_ROLE_RELATION_TABLE_NAME + " b on a.id = b.user_id ");
                INNER_JOIN(ROLE_TABLE_NAME + " c on b.role_id = c.id ");
                WHERE("a.id = #{id}");
            }
        }.toString();
    }
}
