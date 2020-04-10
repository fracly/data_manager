package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Permission;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

/**
 * Role Permission Mapper Interface
 */
@Mapper
public interface RolePermissionMapper {
    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "cn_name", column = "cn_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "operations", column = "operations", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = RolePermissionMapperProvider.class, method = "findPermissionsByRole")
    List<Permission> findPermissionsByRole(@Param("role") Role role);
}
