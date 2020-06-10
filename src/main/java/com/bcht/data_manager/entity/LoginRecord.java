package com.bcht.data_manager.entity;

import org.apache.tools.ant.util.DateUtils;

import java.util.Date;

public class LoginRecord {
    private long id;

    private String name;

    private String username;

    private Date loginTime;

    private String loginIp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + username +  "|" + DateUtils.format(loginTime, "yyyy-MM-dd HH:mm:ss") + "|" + loginIp;
    }
}
