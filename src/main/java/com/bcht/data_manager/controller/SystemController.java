package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.Permission;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.service.SystemService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 系统Controller
 * 对应系统的管理，权限控制
 *      1、菜单管理
 *      2、角色管理
 *      3、用户管理
 *
 *  @author fracly
 *  @date 2020-06-01 15:18:00
 */
@RestController
@RequestMapping("/api/system")
public class SystemController extends BaseController {
    public static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemService systemService;

    @PostMapping("/create-permission")
    public Result createPermission(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, String cn_name) {
        Permission permission = new Permission();
        permission.setCn_name(cn_name);
        permission.setName(name);
        permission.setStatus(1);
        Result result = systemService.createPermission(permission);
        return result;
    }

    @PostMapping("/create-role")
    public Result createRole(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, String cn_name, Integer status, String desc) {
        Role role = new Role();
        role.setCn_name(cn_name);
        role.setName(name);
        role.setStatus(1);
        role.setCreateTime(new Date());
        role.setCreatorId(loginUser.getId());
        role.setDesc(desc);
        Result result = systemService.createRole(role);
        return result;
    }

    @PostMapping("/create-user")
    public Result createUser(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, String name, String username, String password, String phone, String email, String desc) {
        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setPassword(password);
        user.setStatus(1);
        user.setPhone(phone);
        user.setEmail(email);
        user.setDesc(desc);
        user.setCreateTime(new Date());
        user.setCreatorId(loginUser.getId());
        user.setErrorCount(0);
        Result result = systemService.createUser(user);
        return result;
    }

    @GetMapping("/search-permission")
    public Result searchPermission(String name, int status){
        return systemService.searchPermission(name, status);
    }

    @GetMapping("/search-role")
    public Result searchRole(String name, int status){
        return systemService.searchRole(name, status);
    }

    @GetMapping("/search-user")
    public Result searchUser(String name, int status){
        return systemService.searchUser(name, status);
    }

    @GetMapping("/delete-permission")
    public Result deletePermission(int id){
        return systemService.deletePermission(id);
    }

    @GetMapping("/delete-role")
    public Result deleteRole(int id){
        return systemService.deleteRole(id);
    }

    @GetMapping("/delete-user")
    public Result deleteUser(int id){
        return systemService.deleteUser(id);
    }

    @GetMapping("/disable-permission")
    public Result disablePermission(int id){
        return systemService.disablePermission(id);
    }

    @GetMapping("/disable-role")
    public Result disableRole(int id){
        return systemService.disableRole(id);
    }

    @GetMapping("/disable-user")
    public Result disableUser(int id){
        return systemService.disableUser(id);
    }

    @GetMapping("/enable-permission")
    public Result enablePermission(int id){
        return systemService.enablePermission(id);
    }

    @GetMapping("/enable-role")
    public Result enableRole(int id){
        return systemService.enableRole(id);
    }

    @GetMapping("/enable-user")
    public Result enableUser(int id){
        return systemService.enableUser(id);
    }

    @PostMapping("/update-permission")
    public Result updatePermission(String name, String cn_name, int id){
        return systemService.updatePermission(id, name, cn_name);
    }

    @PostMapping("/update-role")
    public Result updateRole(String name, String cn_name, int id, String desc){
        return systemService.updateRole(id, name, cn_name, desc);
    }

    @PostMapping("/update-user")
    public Result updateUser(int id, String name, String username, String password, String phone, String email, String desc) {
        return systemService.updateUser(id, name, username, password, phone, email, desc);
    }

    @PostMapping("/user/modify-password")
    public Result userModifyPassword(int id, String password) {
        return systemService.userModifyPassword(id, password);
    }

    @GetMapping("/role-permission/list")
    public Result rolePermissionList(Integer roleId){
        return systemService.rolePermissionList(roleId);
    }

    @PostMapping("/role-permission/update")
    public Result updateRolePermission(Integer roleId, String idArray){
        return systemService.updateRolePermission(roleId, idArray);
    }

    @GetMapping("/user-role/list")
    public Result userRoleList(Integer userId){
        return systemService.userRoleList(userId);
    }

    @PostMapping("/user-role/update")
    public Result userRoleUpdate(Integer userId, String idArray){
        return systemService.userRoleUpdate(userId, idArray);
    }
}
