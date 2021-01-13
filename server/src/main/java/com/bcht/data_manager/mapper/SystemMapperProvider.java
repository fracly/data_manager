package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class SystemMapperProvider {

    private static final String PERMISSION_TABLE_NAME = "t_data_manager_permission";

    private static final String ROLE_TABLE_NAME = "t_data_manager_role";

    private static final String USER_TABLE_NAME = "t_data_manager_user";

    private static final String ROLE_PERMISSION_RELATION_TABLE_NAME = "t_data_manager_relation_role_permission";

    private static final String USER_ROLE_RELATION_TABLE_NAME = "t_data_manager_relation_user_role";

    private static final String TEMPLATE_TABLE_NAME = "t_data_manager_data_template";

    /**
     * insert data
     */
    public String insertPermission(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(PERMISSION_TABLE_NAME);
                VALUES("`name`", "#{permission.name}");
                VALUES("`cn_name`", "#{permission.cn_name}");
                VALUES("`status`", "#{permission.status}");
            }
        }.toString();
    }

    public String insertRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(ROLE_TABLE_NAME);
                VALUES("`name`", "#{role.name}");
                VALUES("`status`", "#{role.status}");
                VALUES("`cn_name`", "#{role.cn_name}");
                VALUES("`desc`", "#{role.desc}");
                VALUES("`create_time`", "#{role.createTime}");
                VALUES("`creatorId`", "#{role.creatorId}");
            }
        }.toString();
    }

    public String insertUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(USER_TABLE_NAME);
                VALUES("`name`", "#{user.name}");
                VALUES("`username`", "#{user.username}");
                VALUES("`password`", "#{user.password}");
                VALUES("`status`", "#{user.status}");
                VALUES("`phone`", "#{user.phone}");
                VALUES("`email`", "#{user.email}");
                VALUES("`desc`", "#{user.desc}");
                VALUES("`create_time`", "#{user.createTime}");
                VALUES("`error_count`", "#{user.errorCount}");
            }
        }.toString();
    }

    public String insertUserRoleRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(USER_ROLE_RELATION_TABLE_NAME);
                VALUES("`user_id`", "#{userId}");
                VALUES("`role_id`", "#{roleId}");
            }
        }.toString();
    }

    public String insertRolePermissionRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(ROLE_PERMISSION_RELATION_TABLE_NAME);
                VALUES("`role_id`", "#{roleId}");
                VALUES("`permission_id`", "#{permissionId}");
            }
        }.toString();
    }

    /**
     * delete data
     */
    public String deletePermission(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(PERMISSION_TABLE_NAME);
                WHERE("`id`=#{permissionId}");
            }
        }.toString();
    }

    public String deleteRole(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(ROLE_TABLE_NAME);
                WHERE("`id`=#{roleId}");
            }
        }.toString();
    }

    public String deleteUser(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(USER_TABLE_NAME);
                WHERE("`id`=#{userId}");
            }
        }.toString();
    }

    public String deleteUserRoleRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(USER_ROLE_RELATION_TABLE_NAME);
                WHERE("`user_id`= #{userId}");
            }
        }.toString();
    }

    public String deleteRolePermissionRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(ROLE_PERMISSION_RELATION_TABLE_NAME);
                WHERE("`role_id` = #{roleId}");
            }
        }.toString();
    }

    /**
     * update data
     */
    public String disablePermission(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(PERMISSION_TABLE_NAME);
            SET("`status` = 2");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enablePermission(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(PERMISSION_TABLE_NAME);
            SET("`status` = 1");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updatePermission(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(PERMISSION_TABLE_NAME);
            SET("`name` = #{name}");
            SET("`cn_name` = #{cnName}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String disableRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`status` = 9");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enableRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`status` = 1");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateRole(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(ROLE_TABLE_NAME);
            SET("`name` = #{name}");
            SET("`cn_name` = #{cnName}");
            SET("`desc` = #{desc}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String disableUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`status` = 9");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String enableUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`status` = 1");
            SET("`error_count` = 0");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateUser(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`name` = #{name}");
            Object passw = parameter.get("password");
            if (passw != null && StringUtils.isNotEmpty(passw.toString())) {
                SET("`password` = #{password}");
            }
            SET("`phone` = #{phone}");
            SET("`email` = #{email}");
            SET("`desc` = #{desc}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String updateUserLoginInfo(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`last_login_time` = #{lastLoginTime}");
            SET("`last_login_ip` = #{lastLoginIp}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String userModifyPassword(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(USER_TABLE_NAME);
            SET("`password` = #{password}");
            WHERE("`id` = #{id}");
        }}.toString();
    }


    /**
     *search data
     */
    public String searchPermission(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("id, id as 'key', name, cn_name, status, operations");
                FROM(PERMISSION_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("cn_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
            }
        }.toString();
        return sql;
    }

    public String rolePermissionList(Map<String, Object> parameter) {
        String sql = new SQL() {{
            SELECT("permission_id as id");
            FROM(ROLE_PERMISSION_RELATION_TABLE_NAME);
            WHERE("`role_id` = #{roleId}");
        }}.toString();
        return sql;
    }

    public String userRoleList(Map<String, Object> parameter) {
        String sql = new SQL() {{
            SELECT("role_id as id");
            FROM(USER_ROLE_RELATION_TABLE_NAME);
            WHERE("`user_id` = #{userId}");
        }}.toString();
        return sql;
    }


    public String searchRole(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("*, id as 'key'");
                FROM(ROLE_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("cn_name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
                ORDER_BY("create_time");
            }
        }.toString();
        return sql;
    }

    public String searchUser(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("*");
                FROM(USER_TABLE_NAME);
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int status = Integer.parseInt(parameter.get("status").toString());
                if (status != 0) {
                    WHERE(" status =" + status);
                }
                ORDER_BY("create_time");
            }
        }.toString();
        return sql;
    }

    public String dataTemplateList(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("a.*, b.name as creatorName");
                FROM(TEMPLATE_TABLE_NAME + " a");
                INNER_JOIN(USER_TABLE_NAME + " b on a.creatorId = b.id");

                Object name = parameter.get("name");
                if (name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("a.name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }

                Object code = parameter.get("code");
                if (code != null && StringUtils.isNotEmpty(code.toString())) {
                    WHERE("a.code like concat('%', '" + parameter.get("code").toString() + "', '%')");
                }
                ORDER_BY("a.update_time");
            }
        }.toString();
        return sql;
    }

    public String insertTemplate(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(TEMPLATE_TABLE_NAME);
                VALUES("`name`", "#{template.name}");
                VALUES("`code`", "#{template.code}");
                VALUES("`column_json`", "#{template.columnJson}");
                VALUES("`create_time`", "#{template.createTime}");
                VALUES("`update_time`", "#{template.updateTime}");
                VALUES("`creatorId`", "#{template.creatorId}");
            }
        }.toString();
    }

    public String updateTemplate(Map<String, Object> parameter) {
        return new SQL(){{
            UPDATE(TEMPLATE_TABLE_NAME);
            SET("`name` = #{name}");
            SET("`code` = #{code}");
            SET("`column_json` = #{columnJson}");
            SET("`update_time` = #{updateTime}");
            WHERE("`id` = #{id}");
        }}.toString();
    }

    public String deleteTemplate(Map<String, Object> parameter) {
        return new SQL() {{
            DELETE_FROM(TEMPLATE_TABLE_NAME);
            WHERE("`id`=#{id}");
        }}.toString();
    }

}
