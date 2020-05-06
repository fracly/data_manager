package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Data;
import com.bcht.data_manager.entity.Job;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Collection Mapper Interface
 */
@Mapper
public interface CollectionMapper {
    @SelectProvider(type = CollectionMapperProvider.class, method = "jobList")
    List<Map<String, Object>> jobList(@Param("creatorId") int creatorId, @Param("name") String name, @Param("status") int status);

    @InsertProvider(type = CollectionMapperProvider.class, method = "insert")
    int insert(@Param("job") Job job);

}
