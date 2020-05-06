package com.bcht.data_manager.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * Collection Mapper Interface
 */
@Mapper
public interface CollectionMapper {
    @SelectProvider(type = CollectionMapperProvider.class, method = "jobList")
    List<Map<String, Object>> jobList(@Param("creatorId") int creatorId, @Param("name") String name, @Param("status") int status);
}
