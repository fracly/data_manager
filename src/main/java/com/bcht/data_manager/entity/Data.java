package com.bcht.data_manager.entity;

import java.util.Date;

public class Data {
    private long id;

    private String name;

    /**
     * 数据名称
     * type为hive类型， 该字段表示表名
     * type为hbase类型， 该字段表示表名
     * type为hdfs类型， 该字段表示文件完整路径名
     */
    private String dataName;

    private int type;

    private String description;

    private String size;

    private int creatorId;

    private Date createTime;

    private Date updateTime;

    private int status;

    private int destroyMethod;

    private String destroyTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(int destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public String getDestroyTime() {
        return destroyTime;
    }

    public void setDestroyTime(String destroyTime) {
        this.destroyTime = destroyTime;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dataName='" + dataName + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", size='" + size + '\'' +
                ", creatorId=" + creatorId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", status=" + status +
                ", destroyMethod=" + destroyMethod +
                ", destroyTime='" + destroyTime + '\'' +
                '}';
    }
}