package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.Permission;
import com.bcht.data_manager.entity.Role;
import com.bcht.data_manager.entity.Session;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.mapper.RolePermissionMapper;
import com.bcht.data_manager.mapper.SessionMapper;
import com.bcht.data_manager.mapper.UserMapper;
import com.bcht.data_manager.mapper.UserRoleMapper;
import com.bcht.data_manager.utils.EncryptionUtils;
import com.bcht.data_manager.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService extends BaseService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SessionMapper sessionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    public int create(String name, String password, String email, String phone, int type) {
        User user = new User();
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        user.setPassword(EncryptionUtils.getMD5(password));
        user.setType(type);
        return userMapper.insert(user);
    }

    public Result queryById(Integer id) {
        Result result = new Result<>();
        Map<String, Object> map = new HashMap<>();
        User user = userMapper.findById(id);
        List<Role> roleList = userRoleMapper.findRolesByUser(id);
        if(roleList.size() > 0) {
            Role firstRole = roleList.get(0);
            Map<String, Object> roleObjMap = new HashMap();
            roleObjMap.put("id", firstRole.getId());
            roleObjMap.put("name", firstRole.getName());
            roleObjMap.put("describe", firstRole.getDesc());
            roleObjMap.put("creatorId", "system");
            roleObjMap.put("createTime", 1497160610259L);
            roleObjMap.put("deleted", 0);

            // deal with permission
            List<Permission> permissions = rolePermissionMapper.findPermissionsByRole(firstRole);
            List<Map<String, Object>> list = new ArrayList();
            for(int i = 0; i < permissions.size(); i ++) {
                Map<String, Object> permissionObjMap = new HashMap<>();
                permissionObjMap.put("roleId", firstRole.getName());
                permissionObjMap.put("permissionId", permissions.get(i).getName());
                permissionObjMap.put("permissionName", permissions.get(i).getCn_name());
                permissionObjMap.put("actions", permissions.get(i).getOperations());
                permissionObjMap.put("actionList", null);
                permissionObjMap.put("dataAccess", null);
                list.add(permissionObjMap);
            }
            roleObjMap.put("permissions", list);
            map.put("role", roleObjMap);
        }
        result.setData(user);
        result.setDataMap(map);
        result.setCode(0);
        result.setMsg("获取用户信息成功");
        return result;
    }
    public User queryByName(String name) {
        return userMapper.findByName(name);
    }

    public void signOut(String ip, User loginUser) {
        Session session = sessionMapper.queryByUserIdAndIp(loginUser.getId(), ip);
        sessionMapper.deleteById(session.getId());
    }

    public User auth(String username, String password) {
        User user = userMapper.findByName(username);
        if(user != null){
            if(password.equals(user.getPassword())){
                userMapper.cleanErrorCount(username);
                return user;
            } else {
                userMapper.increseErrorCount(username);
                return null;
            }
        }
        return null;
    }

    public int logLogin(String name, String username, String ip) {
        return userMapper.insertLoginLog(name, username, new Date(), ip);
    }

    public int queryErrorCount(String username) {
        return userMapper.queryErrorCount(username);
    }
}
