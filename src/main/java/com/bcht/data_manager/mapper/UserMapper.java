package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * User Mapper Interface
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * from USER WHERE id = #{id}")
    User findById(@Param("id") Integer id);

    @Insert("Insert into user (name, password_md5, type, email, phone, create_time, update_time) " +
            "values (#{user.name}, #{user.passwordMD5}, #{user.type}, #{user.email}, #{user.phone}, " +
            "#{user.createTime}, #{user.updateTime})")
    Integer insert(@Param("user") User user);
}
