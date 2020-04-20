package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.DataSource;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

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

    @InsertProvider(type = DataMapperProvider.class, method = "insertRelation")
    int insertRelation(@Param("dataId") int dataId, @Param("dataSourceId") int dataSourceId);

    /**
     * delete data
     */
    @DeleteProvider(type = DataMapperProvider.class, method = "delete")
    int deleteById(@Param("dataId") int dataId);

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

    /**
     * query by searchVal within name column
     */
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
    @SelectProvider(type = DataMapperProvider.class, method = "queryByName")
    List<Data> queryByName(@Param("name") String name);

    @SelectProvider(type = DataMapperProvider.class, method = "queryByUserId")
    List<Data> queryByUserId(@Param("userId") int userId);

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
    @SelectProvider(type = DataMapperProvider.class, method = "listByDataSource")
    List<Data> listByDataSource(@Param("dataSourceId") int dataSourceId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @SelectProvider(type = DataMapperProvider.class, method = "countByDataSource")
    int countByDataSource(@Param("dataSourceId") int dataSourceId);

    @SelectProvider(type = DataMapperProvider.class, method = "queryMaxId")
    int queryMaxId();
}