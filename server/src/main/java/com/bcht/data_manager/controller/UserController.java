package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.service.SessionService;
import com.bcht.data_manager.service.UserService;
import com.bcht.data_manager.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController{
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Integer create(String name, String password, String email, String phone, int type) {
        return userService.create(name, password, email, phone, type);
    }

    /**
     * get user info
     */
    @GetMapping("/info")
    public Result info(@RequestAttribute(value = Constants.SESSION_USER) User loginUser){

        return userService.queryById(loginUser.getId());
    }

    /**
     * get user by id
     */
    @GetMapping("/queryById")
    @ResponseBody
    public Result queryById(int id) {
        return userService.queryById(id);
    }

    /**
     * get user by name
     */
    @GetMapping("/queryByName")
    @ResponseBody
    public User queryByName(String username) {
        return userService.queryByName(username);
    }
}
