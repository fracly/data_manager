package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User Role Mapper Interface
 */
@Mapper
public interface DataSourceMapper {

    /**
     * insert datasource
     */
    @InsertProvider(type = DataSourceMapperProvider.class, method = "insert")
    int insert(@Param("dataSource") DataSource dataSource);


    /**
     * delete datasource
     */
    @DeleteProvider(type = DataSourceMapperProvider.class, method = "delete")
    int deleteById(@Param("dataSourceId") int dataSourceId);


    /**
     * update session
     */
    @UpdateProvider(type = DataSourceMapperProvider.class, method = "update")
    int update(@Param("dataSource") DataSource dataSource);


    /**
     * query by datasource id
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ip", column = "ip", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "port", column = "port", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "category1", column = "category1", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = DataSourceMapperProvider.class, method = "queryById")
    DataSource queryById(@Param("dataSourceId") int dataSourceId);

    /**
     * query datasource list by user id
     */
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "ip", column = "ip", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "port", column = "port", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "category1", column = "category1", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
    })
    @SelectProvider(type = DataSourceMapperProvider.class, method = "query")
    List<DataSource> query(@Param("userId") int userId, int type, String name);


    @SelectProvider(type = DataSourceMapperProvider.class, method = "statistic")
    List<Map<String, Integer>> statistic(@Param("creatorId") int creatorId);
}
