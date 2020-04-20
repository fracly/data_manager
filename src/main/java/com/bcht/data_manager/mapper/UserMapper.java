package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * User Mapper Interface
 */
@Mapper
public interface UserMapper {

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

    @Select("SELECT * from t_data_manager_user WHERE username = #{username}")
    @Results({
            @Result(id = true, property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "type", column = "type"),
            @Result(property = "email", column = "email"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    User findByName(@Param("username") String username);
}