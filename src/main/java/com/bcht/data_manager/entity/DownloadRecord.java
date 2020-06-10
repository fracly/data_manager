package com.bcht.data_manager.entity;

import org.apache.tools.ant.util.DateUtils;

import java.util.Date;

public class DownloadRecord {
    private long id;

    private String name;

    private String username;

    private Date startTime;

    private String dataName;

    public int status;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + username +  "|" + dataName + "|" + DateUtils.format(startTime, "yyyy-MM-dd HH:mm:ss") + "|" + status;
    }
}
