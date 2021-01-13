package com.bcht.data_manager.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * Search Mapper Interface
 */
@Mapper
public interface SearchMapper {

    /**
     * insert search log
     */
    @InsertProvider(type = SearchMapperProvider.class, method = "insert")
    int insert(@Param("userId") int userId, @Param("name") String name);

    @SelectProvider(type = SearchMapperProvider.class, method = "userActive")
    List<Map<String, Object>> userActive(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = SearchMapperProvider.class, method = "searchKeyword")
    List<Map<String, Object>> searchKeyword(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = SearchMapperProvider.class, method = "searchCountByDay")
    List<Map<String, Object>> searchCountByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = SearchMapperProvider.class, method = "searchUserByDay")
    List<Map<String, Object>> searchUserByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = SearchMapperProvider.class, method = "searchUserTotal")
    Integer searchUserTotal(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @SelectProvider(type = SearchMapperProvider.class, method = "loginCountByDay")
    List<Map<String, Object>> loginCountByDay(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
