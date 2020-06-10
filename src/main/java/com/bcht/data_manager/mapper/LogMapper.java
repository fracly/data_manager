package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * Log Mapper Interface
 */
@Mapper
public interface LogMapper {

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "username", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "loginTime", column = "login_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "loginIp", column = "login_ip", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = LogMapperProvider.class, method = "loginLog")
    List<LoginRecord> loginLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset")Integer offset, @Param("pageSize")Integer pageSize);

    @SelectProvider(type = LogMapperProvider.class, method = "countLoginLog")
    int countLoginLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "dataName", column = "data_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "username", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
    })
    @SelectProvider(type = LogMapperProvider.class, method = "downloadLog")
    List<DownloadRecord> downloadLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset")Integer offset, @Param("pageSize")Integer pageSize);

    @SelectProvider(type = LogMapperProvider.class, method = "countDownloadLog")
    int countDownloadLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "username", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "keyword", column = "keyword", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "searchTime", column = "search_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
    })
    @SelectProvider(type = LogMapperProvider.class, method = "searchLog")
    List<SearchRecord> searchLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("offset")Integer offset, @Param("pageSize")Integer pageSize);

    @SelectProvider(type = LogMapperProvider.class, method = "countSearchLog")
    int countSearchLog(@Param("name") String name, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
