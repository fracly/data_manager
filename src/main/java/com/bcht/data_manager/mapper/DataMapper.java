package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Mapper Interface
 */
@Mapper
public interface DataMapper {

    /**
     * insert data
     */
    @InsertProvider(type = DataMapperProvider.class, method = "insert")
    int insert(@Param("data") Data data);

    @InsertProvider(type = DataMapperProvider.class, method = "insertDataSourceDataRelation")
    int insertDataSourceDataRelation(@Param("dataId") long dataId, @Param("dataSourceId") long dataSourceId);

    @InsertProvider(type = DataMapperProvider.class, method = "insertLabelDataRelation")
    int insertLabelDataRelation(@Param("dataId") long dataId, @Param("labelId") int labelId);
    /**
     * delete data
     */
    @DeleteProvider(type = DataMapperProvider.class, method = "delete")
    int deleteById(@Param("dataId") int dataId);

    @DeleteProvider(type = DataMapperProvider.class, method = "deleteDataSourceDataRelation")
    int deleteDataSourceDataRelation(@Param("dataId") int dataId);

    @DeleteProvider(type = DataMapperProvider.class, method = "deleteLabelDataRelation")
    int deleteLabelDataRelation(@Param("dataId") int dataId);

    /**
     * update date
     */
    @UpdateProvider(type = DataMapperProvider.class, method = "update")
    int update(@Param("data") Data data);

    /**
     * query by data id
     */
    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dataName", column = "data_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "size", column = "size", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyMethod", column = "destroy_method", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyTime", column = "destroy_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = DataMapperProvider.class, method = "queryById")
    Data queryById(@Param("dataId") int dataId);

    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ip", column = "ip", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "port", column = "port", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "category1", column = "category1", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = DataMapperProvider.class, method = "queryDataSourceByDataId")
    DataSource queryDataSourceByDataId(@Param("dataId") long dataId);

    @SelectProvider(type = DataMapperProvider.class, method = "groupByStatus")
    List<Map<String, Object>> groupByStatus();

    @SelectProvider(type = DataMapperProvider.class, method = "queryDataIdsByLabel")
    List<Integer> queryDataIdsByLabel(@Param("labelId") int labelId);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "dataName", column = "data_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "size", column = "size", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyMethod", column = "destroy_method", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyTime", column = "destroy_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = DataMapperProvider.class, method = "search")
    List<Data> search(@Param("creatorId") int creatorId, @Param("name") String name, @Param("type") int type, @Param("dataIds") String dataIds,
                      @Param("offset") int offset, @Param("pageSize") int pageSize, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = DataMapperProvider.class, method = "searchTotal")
    Integer searchTotal(@Param("creatorId") int creatorId, @Param("name") String name, @Param("type") int type, @Param("dataIds") String dataIds,
                        @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "size", column = "size", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "updateTime", column = "update_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyMethod", column = "destroy_method", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "destroyTime", column = "destroy_time", javaType = String.class, jdbcType = JdbcType.VARCHAR),
    })
    @SelectProvider(type = DataMapperProvider.class, method = "list")
    List<Data> list(@Param("creatorId") int creatorId, @Param("dataSourceId") int dataSourceId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @SelectProvider(type = DataMapperProvider.class, method = "listTotal")
    Integer listTotal(@Param("creatorId") int creatorId, @Param("dataSourceId") int dataSourceId);

    @SelectProvider(type = DataMapperProvider.class, method = "queryMaxId")
    Long queryMaxId();

    /**
     * for analysis dashboard
     */
    @Select("select DATE_FORMAT(start_time,'%Y-%m-%d') as dayStr, count(1) as total from t_data_manager_download_log " +
            "where start_time>=#{startDate} and start_time <=#{endDate} group by dayStr")
    List<Map<String, Object>> countDownloadByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("select DATE_FORMAT(create_time,'%Y-%m-%d') as dayStr, count(1) as total from t_data_manager_data " +
            "where create_time>=#{startDate} and create_time <=#{endDate} group by dayStr")
    List<Map<String, Object>> countIncreaseByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("select a.name, count(b.data_id) as total from t_data_manager_label a inner join t_data_manager_relation_label_data b on a.id =b.label_id  " +
            "inner join t_data_manager_data c on b.data_id =c.id where c.create_time>= #{startDate} and c.create_time <= #{endDate} group by a.name order by count(b.data_id) desc limit 7")
    List<Map<String, Object>> countByLabel(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = DataMapperProvider.class, method = "groupByType")
    List<Map<String, Object>> dateTypePercentage(@Param("startDate") String startDate, @Param("endDate") String endDate);


}
