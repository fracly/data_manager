package com.bcht.data_manager.entity;

import org.apache.tools.ant.util.DateUtils;

import java.util.Date;

public class SearchRecord {
    private long id;

    private String name;

    private String username;

    private String keyword;

    private Date searchTime;

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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    @Override
    public String toString() {
        return id + "|" + name + "|" + username +  "|" + DateUtils.format(searchTime, "yyyy-MM-dd HH:mm:ss") + "|" + keyword;
    }
}
