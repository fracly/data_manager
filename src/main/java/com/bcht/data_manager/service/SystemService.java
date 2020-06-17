package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Permission;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.mapper.SystemMapper;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SystemService extends BaseService {
    public static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    @Autowired
    private SystemMapper systemMapper;

    public Result createPermission(Permission permission) {
        Result result = new Result();
        systemMapper.insertPermission(permission);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建菜单成功");
        return result;
    }

    public Result createRole(Role role) {
        Result result = new Result();
        systemMapper.insertRole(role);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建角色成功");
        return result;
    }

    public Result createUser(User user) {
        Result result = new Result();
        systemMapper.insertUser(user);
        putMsg(result, Status.CUSTOM_SUCESSS, "创建用户成功");
        return result;
    }

    public Result searchPermission(String name, int status) {
        Result result = new Result();
        List<Permission> list = systemMapper.searchPermission(name, status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result rolePermissionList(Integer roleId) {
        Result result = new Result();
        List<Integer> list = systemMapper.rolePermissionList(roleId);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userRoleList(Integer userId) {
        Result result = new Result();
        List<Integer> list = systemMapper.userRoleList(userId);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userRoleUpdate(Integer userId, String idArray) {
        Result result = new Result();
        systemMapper.deleteUserRoleRelation(userId);
        String[] array = idArray.split(",");
        for(String roleId : array) {
            systemMapper.insertUserRoleRelation(userId, Integer.parseInt(roleId));
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "更新用户角色成功");
        return result;
    }


    public Result updateRolePermission(Integer roleId, String idArray) {
        Result result = new Result();
        systemMapper.deleteRolePermissionRelation(roleId);
        String[] array = idArray.split(",");
        for(String permissionId : array) {
            systemMapper.insertRolePermissionRelation(roleId, Integer.parseInt(permissionId));
        }
        putMsg(result, Status.CUSTOM_SUCESSS, "更新角色权限成功");
        return result;
    }


    public Result searchRole(String name, int status) {
        Result result = new Result();
        List<Role> list = systemMapper.searchRole(name, status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result searchUser(String name, int status) {
        Result result = new Result();
        List<User> list = systemMapper.searchUser(name, status);
        result.setData(list);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result deletePermission(int id) {
        Result result = new Result();
        systemMapper.deletePermission(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除权限成功");
        return result;
    }

    public Result deleteRole(int id) {
        Result result = new Result();
        systemMapper.deleteRole(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除角色成功");
        return result;
    }

    public Result deleteUser(int id) {
        Result result = new Result();
        systemMapper.deleteUser(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "删除用户成功");
        return result;
    }

    public Result disablePermission(int id) {
        Result result = new Result();
        systemMapper.disablePermission(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用权限成功");
        return result;
    }

    public Result disableRole(int id) {
        Result result = new Result();
        systemMapper.disableRole(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用角色成功");
        return result;
    }

    public Result disableUser(int id) {
        Result result = new Result();
        systemMapper.disableUser(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用用户成功");
        return result;
    }

    public Result enablePermission(int id) {
        Result result = new Result();
        systemMapper.enablePermission(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "禁用权限成功");
        return result;
    }

    public Result enableRole(int id) {
        Result result = new Result();
        systemMapper.enableRole(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "解封角色成功");
        return result;
    }

    public Result enableUser(int id) {
        Result result = new Result();
        systemMapper.enableUser(id);
        putMsg(result, Status.CUSTOM_SUCESSS, "解封用户成功");
        return result;
    }

    public Result updatePermission(int id, String name, String cn_name) {
        Result result = new Result();
        systemMapper.updatePermission(name, cn_name, id);
        putMsg(result, Status.CUSTOM_SUCESSS, "更新权限成功");
        return result;
    }

    public Result updateRole(int id, String name, String cn_name, String desc) {
        Result result = new Result();
        systemMapper.updateRole(name, cn_name, id, desc);
        putMsg(result, Status.CUSTOM_SUCESSS, "更新角色成功");
        return result;
    }

    public Result updateUser(int id, String name, String username, String password, String phone, String email, String desc) {
        Result result = new Result();
        systemMapper.updateUser(id, name, username, password, phone, email, desc);
        putMsg(result, Status.CUSTOM_SUCESSS, "更新用户成功");
        return result;
    }

    public Result updateUserLoginInfo(int id, Date lastLoginTime, String lastLoginIp) {
        Result result = new Result();
        systemMapper.updateUserLoginInfo(id, lastLoginTime, lastLoginIp);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Result userModifyPassword(int id, String password) {
        Result result = new Result();
        systemMapper.userModifyPassword(id, password);
        putMsg(result, Status.CUSTOM_SUCESSS, "重设密码成功");
        return result;
    }

}
