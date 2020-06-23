package com.bcht.data_manager.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * data mapper provider
 */
public class DataMapperProvider {

    private static final String DATA_TABLE_NAME = "t_data_manager_data";

    public static final String USER_TABLE_NAME = "t_data_manager_user";

    private static final String DATASOURCE_TABLE_NAME = "t_data_manager_datasource";

    private static final String DATASOURCE_DATA_RELATION_TABLE_NAME = "t_data_manager_relation_datasource_data";

    public  static final String LABEL_DATA_RELATION_TABLE_NAME = "t_data_manager_relation_label_data";

    public static final String DATA_DOWNLOAD_LOG_TABLE_NAME = "t_data_manager_download_log";

    /**
     * insert data
     */
    public String insert(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATA_TABLE_NAME);
                VALUES("`id`", "#{data.id}");
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
                VALUES("`zz_public`", "#{data.zzPublic}");
                VALUES("`zz_encrypt`", "#{data.zzEncrypt}");
            }
        }.toString();
    }

    public String insertDownload(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATA_DOWNLOAD_LOG_TABLE_NAME);
                VALUES("`data_id`", "#{data.id}");
                VALUES("`data_name`", "#{data.dataName}");
                VALUES("`user_id`", "#{userId}");
                VALUES("`status`", "#{data.status}");
                VALUES("`start_time`", "now()");
                VALUES("`end_time`", "now()");
            }
        }.toString();
    }

    public String insertDataSourceDataRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(DATASOURCE_DATA_RELATION_TABLE_NAME);
                VALUES("`data_id`", "#{dataId}");
                VALUES("`datasource_id`", "#{dataSourceId}");
            }
        }.toString();
    }

    public String insertLabelDataRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                INSERT_INTO(LABEL_DATA_RELATION_TABLE_NAME);
                VALUES("`data_id`", "#{dataId}");
                VALUES("`label_id`", "#{labelId}");
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

    public String deleteDataSourceDataRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(DATASOURCE_DATA_RELATION_TABLE_NAME);
                WHERE("`data_id`=#{dataId}");
            }
        }.toString();
    }

    public String deleteLabelDataRelation(Map<String, Object> parameter) {
        return new SQL() {
            {
                DELETE_FROM(LABEL_DATA_RELATION_TABLE_NAME);
                WHERE("`data_id`=#{dataId}");
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
                SET("`data_name` = #{data.dataName}");
                SET("`creatorId` = #{data.creatorId}");
                SET("`update_time` = #{data.updateTime}");
                SET("`description` = #{data.description}");
                SET("`status` = #{data.status}");
                SET("`zz_public` = #{data.zzPublic}");
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

    public String queryDataSourceByDataId(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*");
                FROM(DATASOURCE_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.datasource_id ");
                INNER_JOIN(DATA_TABLE_NAME + " c on b.data_id = c.id ");
                WHERE("c.id = #{dataId}");
                LIMIT(1);
            }
        }.toString();
    }

    public String queryDataIdsByLabel(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("data_id");
                FROM(LABEL_DATA_RELATION_TABLE_NAME);
                WHERE("label_id = #{labelId}");
            }
        }.toString();
    }

    public String search(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("*");
                FROM(DATA_TABLE_NAME);
                WHERE("(creatorId = " + parameter.get("creatorId").toString() + " or zz_public = 1)");
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', '" + parameter.get("name").toString() + "', '%')");
                }
                int type = Integer.parseInt(parameter.get("type").toString());
                if (type != 0) {
                    WHERE(" type =" + type);
                }
                Object dataIds = parameter.get("dataIds");
                if (dataIds != null && StringUtils.isNotEmpty(dataIds.toString())) {
                    WHERE(" id in (" + dataIds + ")");
                }
                Object startDate = parameter.get("startDate");
                Object endDate = parameter.get("endDate");
                if(startDate != null && endDate != null &&
                        StringUtils.isNotEmpty(startDate.toString()) && StringUtils.isNotEmpty(endDate.toString())) {
                    WHERE(" create_time >= '" + startDate.toString() + "' and create_time <= '" + endDate.toString() + "'");
                }
                ORDER_BY("update_time limit " + parameter.get("offset").toString() + ", " + parameter.get("pageSize"));
            }
        }.toString();
        return sql;
    }

    public String searchTotal(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("count(1)");
                FROM(DATA_TABLE_NAME);
                WHERE("(creatorId = #{creatorId} or zz_public = 1)");
                Object name = parameter.get("name");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("name like concat('%', #{name}, '%')");
                }
                int type = Integer.parseInt(parameter.get("type").toString());
                if (type != 0) {
                    WHERE(" type = #{type}" );
                }
                Object dataIds = parameter.get("dataIds");
                if (dataIds != null && !StringUtils.isEmpty(dataIds.toString())) {
                    WHERE(" id in (#{dataIds})");
                }
                Object startDate = parameter.get("startDate");
                Object endDate = parameter.get("endDate");
                if(startDate != null && endDate != null &&
                        StringUtils.isNotEmpty(startDate.toString()) && StringUtils.isNotEmpty(endDate.toString())) {
                    WHERE(" create_time >= '" + startDate.toString() + "' and create_time <= '" + endDate.toString() + "'");
                }
            }
        }.toString();
        return sql;
    }

    public String encryptSearch(Map<String, Object> parameter) {
        String sql = new SQL() {
            {
                SELECT("a.*");
                FROM(DATA_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id");
                Object name = parameter.get("searchVal");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("a.name like concat('%', '" + parameter.get("searchVal").toString() + "', '%')");
                }
                int dataSourceId = Integer.parseInt(parameter.get("dataSourceId").toString());
                if (dataSourceId != 0) {
                    WHERE(" b.datasource_id =" + dataSourceId);
                }
                ORDER_BY("a.update_time limit " + parameter.get("offset").toString() + ", " + parameter.get("pageSize"));
            }
        }.toString();
        return sql;
    }

    public String encryptSearchTotal(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(DATA_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id");
                Object name = parameter.get("searchVal");
                if(name != null && StringUtils.isNotEmpty(name.toString())) {
                    WHERE("a.name like concat('%', '" + parameter.get("searchVal").toString() + "', '%')");
                }
                int dataSourceId = Integer.parseInt(parameter.get("dataSourceId").toString());
                if (dataSourceId != 0) {
                    WHERE(" b.datasource_id =" + dataSourceId);
                }
            }
        }.toString();
    }

    /**
     * query data list by user id
     */
    public String groupByStatus(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("status, count(1) as total");
                FROM(DATA_TABLE_NAME);
                GROUP_BY("status");
            }
        }.toString();
    }

    public String groupByType(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("case type when 1 then  'Hive' when 2 then 'HBase' else 'HDFS' end as item,  count(1) as  count");
                FROM(DATA_TABLE_NAME);
                Object startDate = parameter.get("startDate");
                if(startDate != null && StringUtils.isNotEmpty(startDate.toString())) {
                    WHERE("`create_time` >= #{startDate}", "`create_time` <= #{endDate}");
                }
                GROUP_BY("type");
                ORDER_BY("count(1) desc ");
            }
        }.toString();
    }

    public String list(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("a.*, c.username as owner");
                FROM(DATA_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id ");
                INNER_JOIN(USER_TABLE_NAME + " c on a.creatorId = c.id");
                Object dataSourceId = parameter.get("dataSourceId");
                if(dataSourceId != null && StringUtils.isNotEmpty(dataSourceId.toString()) && !dataSourceId.toString().equals("0")){
                    WHERE(" b.datasource_id = #{dataSourceId} ");
                }
                Object searchVal = parameter.get("searchVal");
                if(searchVal != null && StringUtils.isNotEmpty(searchVal.toString())) {
                    WHERE("a.name like concat('%', '" + parameter.get("searchVal").toString() + "', '%')");
                }
                WHERE(" (a.creatorId = #{creatorId} or a.zz_public = 1) ");
                ORDER_BY(" a.id desc limit #{offset}, #{pageSize}");
            }
        }.toString();
    }

    public String listTotal(Map<String, Object> parameter) {
        return new SQL() {
            {
                SELECT("count(1)");
                FROM(DATA_TABLE_NAME + " a ");
                INNER_JOIN(DATASOURCE_DATA_RELATION_TABLE_NAME + " b on a.id = b.data_id ");
                Object dataSourceId = parameter.get("dataSourceId");
                if(dataSourceId != null && StringUtils.isNotEmpty(dataSourceId.toString()) && !dataSourceId.toString().equals("0")){
                    WHERE(" b.datasource_id = #{dataSourceId} ");
                }
                WHERE(" (a.creatorId = #{creatorId} or a.zz_public = 1) ");
            }
        }.toString();
    }

    public String queryMaxId(Map<String, Object> parameter) {
        return new SQL() {{
            SELECT("max(id)");
            FROM(DATA_TABLE_NAME);
        }}.toString();
    }
}
