package com.bcht.data_manager.mapper;

import com.bcht.data_manager.entity.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Data Mapper Interface
 */
@Mapper
public interface SystemMapper {

    /**
     * insert data
     */
    @InsertProvider(type = SystemMapperProvider.class, method = "insertPermission")
    int insertPermission(@Param("permission") Permission permission);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertRole")
    int insertRole(@Param("role") Role role);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertUser")
    int insertUser(@Param("user") User user);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertRolePermissionRelation")
    int insertRolePermissionRelation(@Param("roleId") int roleId, @Param("permissionId") int permissionId);

    @InsertProvider(type = SystemMapperProvider.class, method = "insertUserRoleRelation")
    int insertUserRoleRelation(@Param("userId") int userId, @Param("roleId") int roleId);


    /**
     * delete data
     */
    @DeleteProvider(type = SystemMapperProvider.class, method = "deletePermission")
    int deletePermission(@Param("id") int id);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteRole")
    int deleteRole(@Param("id") int id);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteUser")
    int deleteUser(@Param("id") int id);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteUserRoleRelation")
    int deleteUserRoleRelation(@Param("userId") int userId);

    @DeleteProvider(type = SystemMapperProvider.class, method = "deleteRolePermissionRelation")
    int deleteRolePermissionRelation(@Param("roleId") int roleId);

    /**
     * update data
     */
    @UpdateProvider(type = SystemMapperProvider.class, method = "disablePermission")
    int disablePermission(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enablePermission")
    int enablePermission(@Param("id") int id);


    @UpdateProvider(type = SystemMapperProvider.class, method = "updatePermission")
    int updatePermission(@Param("name") String name, @Param("cnName") String cnName, @Param("id") Integer id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "disableRole")
    int disableRole(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enableRole")
    int enableRole(@Param("id") int id);


    @UpdateProvider(type = SystemMapperProvider.class, method = "updateRole")
    int updateRole(@Param("name") String name, @Param("cnName") String cnName, @Param("id") Integer id, @Param("desc") String desc);

    @UpdateProvider(type = SystemMapperProvider.class, method = "disableUser")
    int disableUser(@Param("id") int id);

    @UpdateProvider(type = SystemMapperProvider.class, method = "enableUser")
    int enableUser(@Param("id") int id);


    @UpdateProvider(type = SystemMapperProvider.class, method = "updateUser")
    int updateUser(@Param("id") Integer id, @Param("name") String name, @Param("username") String username,
                   @Param("password") String password, @Param("phone") String phone, @Param("email") String email,  @Param("desc") String desc);

    @UpdateProvider(type = SystemMapperProvider.class, method = "updateUserLoginInfo")
    int updateUserLoginInfo(@Param("id") Integer id, @Param("lastLoginTime") Date lastLoginTime, @Param("lastLoginIp") String lastLoginIp);

    @UpdateProvider(type = SystemMapperProvider.class, method = "userModifyPassword")
    int userModifyPassword(@Param("id") Integer id, @Param("password") String password);

    /**
     * search data
     */
    @SelectProvider(type = SystemMapperProvider.class, method = "searchPermission")
    List<Permission> searchPermission(@Param("name") String name, @Param("status") int status);

    @SelectProvider(type = SystemMapperProvider.class, method = "rolePermissionList")
    List<Integer> rolePermissionList(@Param("roleId") Integer roleId);

    @SelectProvider(type = SystemMapperProvider.class, method = "userRoleList")
    List<Integer> userRoleList(@Param("userId") Integer userId);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "cn_name", column = "cn_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = SystemMapperProvider.class, method = "searchRole")
    List<Role> searchRole(@Param("name") String name, @Param("status") int status);

    @Results(value = {@Result(property = "id", column = "id", id=true, javaType = Integer.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "username", column = "username", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "password", column = "password", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "status", column = "status", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "email", column = "email", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "create_time", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "desc", column = "desc", javaType = String.class, jdbcType = JdbcType.VARCHAR)
    })
    @SelectProvider(type = SystemMapperProvider.class, method = "searchUser")
    List<User> searchUser(@Param("name") String name, @Param("status") int status);

}
