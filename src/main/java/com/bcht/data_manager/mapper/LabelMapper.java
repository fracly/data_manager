package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Label;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Label Mapper Interface
 */
@Mapper
public interface LabelMapper {
    @SelectProvider(type = LabelMapperProvider.class, method = "list")
    List<Label> list(@Param("creatorId")int creatorId);

    @SelectProvider(type = LabelMapperProvider.class, method = "queryById")
    Label queryById(@Param("labelId") int labelId);

    @SelectProvider(type = LabelMapperProvider.class, method = "queryByName")
    List<Label> queryByName(@Param("labelName") String labelName);

    @InsertProvider(type = LabelMapperProvider.class, method = "insert")
    int insert(@Param("label") Label label);

    @DeleteProvider(type = LabelMapperProvider.class, method = "delete")
    int delete(@Param("labelId") int labelId);

    @UpdateProvider(type = LabelMapperProvider.class, method = "update")
    int update(@Param("label") Label label);

}
