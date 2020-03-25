package com.bcht.data_manager.service;

import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.mapper.UserMapper;
import com.bcht.data_manager.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public int create(String name, String password, String email, String phone, int type) {
        User user = new User();
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setEmail(email);
        user.setPhone(phone);
        user.setName(name);
        user.setPasswordMD5(EncryptionUtils.getMD5(password));
        user.setType(type);
        return userMapper.insert(user);
    }

    public User queryById(Integer id) {
        return userMapper.findById(id);
    }
}
