/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bcht.data_manager.enums;

/**
 *  status enum
 */
public enum Status {

    SUCCESS(0, "success"),
    FAILED(1, "failed"),
    CUSTOM_SUCESSS(0, "{0}"),
    CUSTOM_FAILED(1, "{0}"),

    // Hive 错误码
    HIVE_JDBC_DRIVER_CLASS_NOT_FOUNT(10001, "Hive的驱动类没有找到"),
    HIVE_DROP_DATABASE_FAILED(10002, "删除Hive数据库失败，删库要保证该库为空"),
    HIVE_CREATE_DATABASE_FAILED(10003, "创建Hive数据库失败, 该库已存在"),
    HIVE_PREVIEW_TABLE_DATA_FAILED(10004, "预览Hive表数据失败"),
    HIVE_CONNECTION_TEST_FAILED(10005, "Hive连接测试失败"),
    HIVE_CREATE_TABLE_FAILED(10006, "Hive创建表失败"),
    HIVE_QUERY_HIVE_COLUMN_DETAIL_FAILED(10007, "查询Hive字段详情失败"),
    HIVE_DROP_TABLE_FAILED(10008, "HIVE表删除失败"),
    HIVE_TABLE_ADD_COLUMN_FAILED(10009, "HIVE表添加字段失败"),


    // HBase 错误码
    HBASE_DROP_NAMESPACE_FAILED(20001, "Hbase删除表空间失败"),
    HBASE_CREATE_NAMESPACE_FAILED(20002, "Hbase创建表空间失败"),
    HBASE_CONNECTION_TEST_FAILED(20004, "Hbase连接测试失败"),
    HBASE_CREATE_TABLE_FAILED(20005, "Hbase创建表失败"),
    HBASE_QUERY_HBASE_COLUMN_FAMILY_DETAIL_FAILED(20006, "查询HBase字段簇详情失败"),
    HBASE_DROP_TABLE_FAILED(20007, "HBASE表删除失败"),
    HBASE_PREVIEW_TABLE_DATA_FAILED(20008, "预览HBase表数据失败"),

    //HDFS 错误码
    HDFS_REMOVE_DIRECTORY_FAILED(30001, "HDFS删除目录失败, 请检查该目录是否为空目录"),
    HDFS_CREATE_DIRECTORY_FAILED(30002, "HDFS创建目录失败，该目录已存在"),
    HDFS_CONNECTION_TEST_FAILED(30004, "HDFS连接测试失败"),
    HDFS_COPY_UPLOAD_TO_LOCAL_FAILED(30005, "上传文件到本地出错"),
    HDFS_COPY_LOCAL_TO_HDFS_FAILED(30006, "本地文件复制到HDFS上出错"),
    HDFS_QUERY_HDFS_FILE_DETAIL_FAILED(30007, "查询HDFS文件详情失败"),
    HDFS_DELETE_FILE_FAILED(30008, "HDFS文件删除失败"),
    HDFS_PREVIEW_FILE_DATA_FAILED(30009, "预览HDFS文件失败"),

    UNKOWN_DATASOURCE_TYPE(90001, "未知的数据源类型"),

    SQLSERVER_JDBC_DRIVER_CLASS_NOT_FOUNT(11001, "SqlServer驱动类没有找到"),
    MYSQL_JDBC_DRIVER_CLASS_NOT_FOUNT(11002, "Mysql驱动类没有找到"),
    ORACLE_JDBC_DRIVER_CLASS_NOT_FOUNT(11003, "Oracle驱动类没有找到"),
    DB2_JDBC_DRIVER_CLASS_NOT_FOUNT(11004, "DB2驱动类没有找到"),

    GET_INPUT_DATASOURCE_FAILED(11005, "获取输入源连接失败"),
    NOT_FOUND_TABLE_IN_THIS_DATABASE(11006, "在该数据库中没有找到该表"),

    // 数据采集相关
    EXECUTE_SHELL_FAILED(12001, "shell脚本执行失败"),
    FILE_UPLOAD_FAILED(12002, "文件上传至服务器本地失败"),
    LOAD_LOCAL_FILE_TO_HIVE_TABLE_FAILED(12003, "本地文件上传至Hive表失败-load data local"),
    LOAD_HDFS_FILE_TO_HIVE_TABLE_FAILED(12004, "HDFS文件上传至Hive表失败- load data hdfs"),
    ;

    private int code;
    private String msg;

    private Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
