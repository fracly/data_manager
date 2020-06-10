package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.Session;
import com.bcht.data_manager.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.sql.Timestamp;
import java.util.List;

/**
 * User Role Mapper Interface
 */
@Mapper
public interface UserRoleMapper {

    @Select("SELECT * from t_data_manager_user WHERE id = #{id}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "avatar", column = "avatar"),
            @Result(property = "status", column = "status"),
            @Result(property = "type", column = "type"),
            @Result(property = "email", column = "email"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    User findById(@Param("id") Integer id);

    @Insert("insert into t_user (name, password_md5, type, email, phone, create_time, update_time) " +
            "values (#{user.name}, #{user.passwordMD5}, #{user.type}, #{user.email}, #{user.phone}, " +
            "#{user.createTime}, #{user.updateTime})")
    Integer insert(@Param("user") User user);

    @Results(value = {@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "cn_name", column = "cn_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = UserRoleMapperProvider.class, method = "findRolesByUser")
    List<Role> findRolesByUser(@Param("id") Integer id);

}
