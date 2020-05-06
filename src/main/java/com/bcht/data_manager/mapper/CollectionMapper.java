package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.Job;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Collection Mapper Interface
 */
@Mapper
public interface CollectionMapper {
    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "type", column = "type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "inputType", column = "input_type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "inputParameter", column = "input_parameter", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "outputType", column = "output_type", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "outputId", column = "output_id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "startTime", column = "start_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "endTime", column = "end_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = CollectionMapperProvider.class, method = "jobList")
    List<Job> jobList(@Param("creatorId") int creatorId, @Param("status") int status);

    @DeleteProvider(type = CollectionMapperProvider.class, method = "jobDelete")
    int jobDelete(@Param("id") long id);

    @InsertProvider(type = CollectionMapperProvider.class, method = "insert")
    int insert(@Param("job") Job job);

}
