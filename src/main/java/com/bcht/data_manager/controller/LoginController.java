package com.bcht.data_manager.controller;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.User;
import com.bcht.data_manager.enums.Status;
import com.bcht.data_manager.service.SessionService;
import com.bcht.data_manager.service.SystemService;
import com.bcht.data_manager.service.UserService;
import com.bcht.data_manager.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/login")
public class LoginController extends BaseController{
    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SystemService systemService;

    /**
     * logout
     */
    @PostMapping("/signOut")
    public Result signOut(@RequestAttribute(value = Constants.SESSION_USER) User loginUser, HttpServletRequest request, HttpServletResponse response){
        logger.info("login user:{} sign out", loginUser.getUsername());
        String ip = getClientIpAddress(request);
        userService.signOut(ip, loginUser);
        return success("登出成功");
    }

    /**
     * auth user
     */
    @PostMapping("/auth")
    @ResponseBody
    public Result auth(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {

        logger.info("login user name: {} ", user.getUsername());
        Result result = new Result();
        if(user == null || StringUtils.isEmpty(user.getUsername())) {
            result.setMsg("参数缺失，请填写用户名");
            result.setCode(-1);
            return result;
        }

        if(StringUtils.isEmpty(user.getPassword())) {
            result.setMsg("参数缺失，请填写密码");
            result.setCode(-1);
            return result;
        }

        // user ip check
        String ip = getClientIpAddress(request);
        if (StringUtils.isEmpty(ip)) {
            result.setMsg("IP为空");
            result.setCode(-1);
            return result;
        }

        //verify name/password
        int errorCount = userService.queryErrorCount(user.getUsername());
        if (errorCount >= 5) {
            result.setCode(-1);
            result.setMsg("该账号已经被锁定，请联系管理员");
            User user1 = userService.queryByName(user.getUsername());
            if(user1 != null) {
                systemService.disableUser(user1.getId());
            }

            return result;
        }
        User userE = userService.auth(user.getUsername(), user.getPassword());
        if(userE == null) {
            result.setMsg("账号或密码错误");
            result.setCode(-1);
            return result;
        }

        // create session
        String sessionId = sessionService.createSession(userE, ip);

        if(sessionId == null) {
            result.setMsg("创建会话失败");
            result.setCode(-1);
            return result;
        }

        String token = UUID.randomUUID().toString();

        userE.setToken(token);

        systemService.updateUserLoginInfo(userE.getId(), new Date(), ip);
        userService.logLogin(userE.getName(), userE.getUsername(), ip);
        result.setCode(0);
        result.setMsg("登录成功");
        Cookie cookie = new Cookie(Constants.SESSION_ID, sessionId);
        cookie.setMaxAge(60 * 60 * 2); //单位秒
        cookie.setPath("/api");
        response.addCookie(cookie);
        logger.info("sessionId = " + sessionId);
        result.setData(userE);
        return result;
    }
}
