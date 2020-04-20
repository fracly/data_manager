package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Label;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.Date;
import java.util.List;

/**
 * Label Mapper Interface
 */
@Mapper
public interface LabelMapper {
    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "total", column = "total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = LabelMapperProvider.class, method = "list")
    List<Label> list(@Param("creatorId")int creatorId, @Param("searchVal") String searchVal);

    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "total", column = "total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = LabelMapperProvider.class, method = "queryById")
    Label queryById(@Param("labelId") int labelId);

    @SelectProvider(type = LabelMapperProvider.class, method = "countData")
    int countData(@Param("labelId") int labelId);

    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "total", column = "total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = LabelMapperProvider.class, method = "queryByName")
    List<Label> queryByName(@Param("labelName") String labelName);


    @InsertProvider(type = LabelMapperProvider.class, method = "insert")
    int insert(@Param("label") Label label);

    @DeleteProvider(type = LabelMapperProvider.class, method = "delete")
    int delete(@Param("labelId") int labelId);

    @UpdateProvider(type = LabelMapperProvider.class, method = "update")
    int update(@Param("label") Label label);

    @Results(value = {@Result(property = "id", column = "id", id = true, javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "creatorId", column = "creatorId", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "total", column = "total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
    })
    @SelectProvider(type = LabelMapperProvider.class, method = "top10")
    List<Label> top10(@Param("creatorId") int creatorId);
}
