package com.bcht.data_manager.controller;

import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.service.UserService;
import com.bcht.data_manager.utils.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * create user
     */
    @PostMapping("/create")
    public Integer create(String name, String password, String email, String phone, int type) {
        return userService.create(name, password, email, phone, type);
    }

    /**
     * get user by id
     */
    @GetMapping("queryById")
    @ResponseBody
    public User queryById(int id) {
        return userService.queryById(id);
    }


}
