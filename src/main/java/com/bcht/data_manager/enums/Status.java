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

    REQUEST_PARAMS_NOT_VALID_ERROR(10001, "任务参数不正确，请检查！"),
    TASK_TIMEOUT_PARAMS_ERROR(10002, "任务超时参数不正确，请检查！"),
    USER_NAME_EXIST(10003, "用户名已存在！"),
    USER_NAME_NULL(10004,"用户名不可为空！"),
//    DB_OPERATION_ERROR(10005, "database operation error"),
    HDFS_OPERATION_ERROR(10006, "HDFS操作失败！"),
    UPDATE_FAILED(10007, "更新模型实例失败！"),
    TASK_INSTANCE_HOST_NOT_FOUND(10008, "任务实例没有对应主机节点！"),
    TENANT_NAME_EXIST(10009, "租户编号已经存在！"),
    USER_NOT_EXIST(10010, "用户不存在！"),
    ALERT_GROUP_NOT_EXIST(10011, "未找到告警组！"),
    ALERT_GROUP_EXIST(10012, "告警组已经存在！"),
    USER_NAME_PASSWD_ERROR(10013,"用户名或者密码错误！"),
    LOGIN_SESSION_FAILED(10014,"创建会话失败！"),
    DATASOURCE_EXIST(10015, "数据源名称已经存在！"),
    DATASOURCE_CONNECT_FAILED(10016, "数据源连接失败"),
    TENANT_NOT_EXIST(10017, "租户不存在！"),
    PROJECT_NOT_FOUNT(10018, "模型组不存在！"),
    PROJECT_ALREADY_EXISTS(10019, "模型组已存在！"),
    TASK_INSTANCE_NOT_EXISTS(10020, "任务实例不存在！"),
    TASK_INSTANCE_NOT_SUB_WORKFLOW_INSTANCE(10021, "任务实例不是一个子工作流实例！"),
    SCHEDULE_CRON_NOT_EXISTS(10022, "调度crontab不存在！"),
    SCHEDULE_CRON_ONLINE_FORBID_UPDATE(10023, "在线状态不运行修改实例定义！"),
    SCHEDULE_CRON_CHECK_FAILED(10024, "调度crontab验证失败！"),
    MASTER_NOT_EXISTS(10025, "主节点不存在！"),
    SCHEDULE_STATUS_UNKNOWN(10026, "未知命令！"),
    CREATE_ALERT_GROUP_ERROR(10027,"创建告警组失败！"),
    QUERY_ALL_ALERTGROUP_ERROR(10028,"查询全部告警组失败！"),
    LIST_PAGING_ALERT_GROUP_ERROR(10029,"展示分页告警组失败！"),
    UPDATE_ALERT_GROUP_ERROR(10030,"更新模型实例告警组失败！"),
    DELETE_ALERT_GROUP_ERROR(10031,"删除告警组失败！"),
    ALERT_GROUP_GRANT_USER_ERROR(10032,"告警组赋权用户出错！"),
    CREATE_DATASOURCE_ERROR(10033,"创建数据源出错！"),
    UPDATE_DATASOURCE_ERROR(10034,"更新模型数据源出错！"),
    QUERY_DATASOURCE_ERROR(10035,"查询数据源出错！"),
    CONNECT_DATASOURCE_FAILURE(10036,"连接数据源出错！"),

    CONNECTION_TEST_FAILURE(10037,"连接测试失败！"),
    DELETE_DATA_SOURCE_FAILURE(10038,"删除数据源失败！"),
    VERFIY_DATASOURCE_NAME_FAILURE(10039,"验证数据源名称失败！"),
    UNAUTHORIZED_DATASOURCE(10040,"未验证的数据源！"),
    AUTHORIZED_DATA_SOURCE(10041,"已验证的数据源！"),
    LOGIN_SUCCESS(10042,"登录成功！"),
    USER_LOGIN_FAILURE(10043,"用户登录失败！"),
    LIST_WORKERS_ERROR(10044,"获取工作节点出错！"),
    LIST_MASTERS_ERROR(10045,"获取主节点出错！"),
    UPDATE_PROJECT_ERROR(10046,"更新模型组失败！"),
    QUERY_PROJECT_DETAILS_BY_ID_ERROR(10047,"通过ID查询模型组详情出错！"),
    CREATE_PROJECT_ERROR(10048,"创建模型组出错！"),
    LOGIN_USER_QUERY_PROJECT_LIST_PAGING_ERROR(10049,"登录用户获取分页模型组失败！"),
    DELETE_PROJECT_ERROR(10050,"删除模型组失败"),
    QUERY_UNAUTHORIZED_PROJECT_ERROR(10051,"查询未授权模型组出错！"),
    QUERY_AUTHORIZED_PROJECT(10052,"查询授权模型组！"),
    QUERY_QUEUE_LIST_ERROR(10053,"查询队列列表出错！"),
    CREATE_RESOURCE_ERROR(10054,"创建资源出错！"),
    UPDATE_RESOURCE_ERROR(10055,"更新资源出错！"),
    QUERY_RESOURCES_LIST_ERROR(10056,"查询资源列表出错！"),
    QUERY_RESOURCES_LIST_PAGING(10057,"查询资源资源分页列表！"),
    DELETE_RESOURCE_ERROR(10058,"删除资源出错！"),
    VERIFY_RESOURCE_BY_NAME_AND_TYPE_ERROR(10059,"验证资源名称和类型出错！"),
    VIEW_RESOURCE_FILE_ON_LINE_ERROR(10060,"在线查看资源文件出错！"),
    CREATE_RESOURCE_FILE_ON_LINE_ERROR(10061,"在线创建资源文件出错！"),
    RESOURCE_FILE_IS_EMPTY(10062,"资源文件为空！"),
    EDIT_RESOURCE_FILE_ON_LINE_ERROR(10063,"在线编辑资源文件出错！"),
    DOWNLOAD_RESOURCE_FILE_ERROR(10064,"下载资源文件出错！"),
    CREATE_UDF_FUNCTION_ERROR(10065 ,"创建UDF函数出错！"),
    VIEW_UDF_FUNCTION_ERROR( 10066,"查看UDF函数出错！"),
    UPDATE_UDF_FUNCTION_ERROR(10067,"更新模型实例UDF函数出错！"),
    QUERY_UDF_FUNCTION_LIST_PAGING_ERROR( 10068,"查询UDF函数分页列表出错！"),
    QUERY_DATASOURCE_BY_TYPE_ERROR( 10069,"通过类型查询数据源出错！"),
    VERIFY_UDF_FUNCTION_NAME_ERROR( 10070,"验证UDF函数名称出错！"),
    DELETE_UDF_FUNCTION_ERROR( 10071,"删除UDF函数出错！"),
    AUTHORIZED_FILE_RESOURCE_ERROR( 10072,"授权资源文件出错！"),
    UNAUTHORIZED_FILE_RESOURCE_ERROR( 10073,"未授权资源文件出错！"),
    UNAUTHORIZED_UDF_FUNCTION_ERROR( 10074,"未授权UDF函数出错！"),
    AUTHORIZED_UDF_FUNCTION_ERROR(10075,"授权UDF函数出错！"),
    CREATE_SCHEDULE_ERROR(10076,"创建调度出错！"),
    UPDATE_SCHEDULE_ERROR(10077,"更新模型实例调度出错！"),
    PUBLISH_SCHEDULE_ONLINE_ERROR(10078,"发布定时任务上线出错！"),
    OFFLINE_SCHEDULE_ERROR(10079,"下线定时任务出错！"),
    QUERY_SCHEDULE_LIST_PAGING_ERROR(10080,"查询调度分页列表出错！"),
    QUERY_SCHEDULE_LIST_ERROR(10081,"查询调度列表出错！"),
    QUERY_TASK_LIST_PAGING_ERROR(10082,"查询任务分页列表出错！"),
    QUERY_TASK_RECORD_LIST_PAGING_ERROR(10083,"查询任务记录分页列表出错！"),
    CREATE_TENANT_ERROR(10084,"创建租户出错！"),
    QUERY_TENANT_LIST_PAGING_ERROR(10085,"查询租户分页列表出错！"),
    QUERY_TENANT_LIST_ERROR(10086,"查询租户列表出错！"),
    UPDATE_TENANT_ERROR(10087,"更新模型实例租户出错！"),
    DELETE_TENANT_BY_ID_ERROR(10088,"删除租户失败！id->{0}"),
    VERIFY_TENANT_CODE_ERROR(10089,"验证租户编码出错！"),
    CREATE_USER_ERROR(10090,"创建用户出错！"),
    QUERY_USER_LIST_PAGING_ERROR(10091,"查询用户分页列表出错！"),
    UPDATE_USER_ERROR(10092,"updateProcessInstance user error"),
    DELETE_USER_BY_ID_ERROR(10093,"通过Id删除用户失败！"),
    GRANT_PROJECT_ERROR(10094,"授权模型组出错！"),
    GRANT_RESOURCE_ERROR(10095,"授权资源文件出错！"),
    GRANT_UDF_FUNCTION_ERROR(10096,"授权UDF函数出错！"),
    GRANT_DATASOURCE_ERROR(10097,"授权数据源出错！"),
    GET_USER_INFO_ERROR(10098,"获取用户信息出错！"),
    USER_LIST_ERROR(10099,"获取用户列表出错！"),
    VERIFY_USERNAME_ERROR(10100,"验证用户名出错！"),
    UNAUTHORIZED_USER_ERROR(10101,"未授权用户失败！"),
    AUTHORIZED_USER_ERROR(10102,"授权用户出错!"),
    QUERY_TASK_INSTANCE_LOG_ERROR(10103,"查看任务实例日志出错！"),
    QUERY_TASK_INSTANCE_RESULT_ERROR(10138,"查看任务实例结果出错！"),
    DOWNLOAD_TASK_INSTANCE_LOG_FILE_ERROR(10104,"下载任务实例日志文件出错！"),
    CREATE_PROCESS_DEFINITION(10105,"创建模型定义！"),
    VERIFY_PROCESS_DEFINITION_NAME_UNIQUE_ERROR(10106,"验证模型定义名称是否唯一出错！"),
    UPDATE_PROCESS_DEFINITION_ERROR(10107,"更新模型实例的模型定义出错！"),
    RELEASE_PROCESS_DEFINITION_ERROR(10108,"发布模型定义出错！"),
    QUERY_DATAIL_OF_PROCESS_DEFINITION_ERROR(10109,"查询模型定义详情出错！"),
    QUERY_PROCCESS_DEFINITION_LIST(10110,"查询模型定义列表！"),
    ENCAPSULATION_TREEVIEW_STRUCTURE_ERROR(10111,"封装  treeview 结构出错！"),
    GET_TASKS_LIST_BY_PROCESS_DEFINITION_ID_ERROR(10112,"通过模型定义ID获取任务列表出错！"),
    QUERY_PROCESS_INSTANCE_LIST_PAGING_ERROR(10113,"查询模型实例分页列表出错！"),
    QUERY_TASK_LIST_BY_PROCESS_INSTANCE_ID_ERROR(10114,"通过模型实例ID查询任务列表出错！"),
    UPDATE_PROCESS_INSTANCE_ERROR(10115,"更新模型实例出错！"),
    QUERY_PROCESS_INSTANCE_BY_ID_ERROR(10116,"通过模型ID查询模型实例出错！"),
    DELETE_PROCESS_INSTANCE_BY_ID_ERROR(10117,"通过ID删除模型实例出错！"),
    QUERY_SUB_PROCESS_INSTANCE_DETAIL_INFO_BY_TASK_ID_ERROR(10118,"通过任务ID查询子模型实例详情信息出错！"),
    QUERY_PARENT_PROCESS_INSTANCE_DETAIL_INFO_BY_SUB_PROCESS_INSTANCE_ID_ERROR(10119,"通过子模型ID查询父模型实例详情信息出错！"),
    QUERY_PROCESS_INSTANCE_ALL_VARIABLES_ERROR(10120,"查询模型实例所有变量出错！"),
    ENCAPSULATION_PROCESS_INSTANCE_GANTT_STRUCTURE_ERROR(10121,"封装模型实例gantt结构出错！"),
    QUERY_PROCCESS_DEFINITION_LIST_PAGING_ERROR(10122,"查询模型定义分页列表出错！"),
    SIGN_OUT_ERROR(10123,"登出错误！"),
    TENANT_CODE_HAS_ALREADY_EXISTS(10124,"租户编号已存在！"),
    IP_IS_EMPTY(10125,"ip不可为空！"),
    SCHEDULE_CRON_REALEASE_NEED_NOT_CHANGE(10126, "定时任务已经发布！ {0} "),
    CREATE_QUEUE_ERROR(10127, "创建队列出错!"),
    QUEUE_NOT_EXIST(10128, "队列不存在 queue -> {0}"),
    QUEUE_VALUE_EXIST(10129, "队列值已存在！value -> {0}"),
    QUEUE_NAME_EXIST(10130, "队列名称已经存在！name -> {0}"),
    UPDATE_QUEUE_ERROR(10131, "更新队列出错！"),
    NEED_NOT_UPDATE_QUEUE(10132, "内容没变化，无需更新！"),
    VERIFY_QUEUE_ERROR(10133,"验证队列出错！"),
    NAME_NULL(10134,"名称不可为空！"),
    NAME_EXIST(10135, "名称 {0} 已存在！"),
    SAVE_ERROR(10136, "保存出错！"),
    DELETE_PROJECT_ERROR_DEFINES_NOT_NULL(10137, "请先删除模型组中的模型定义！"),
    BATCH_DELETE_PROCESS_INSTANCE_BY_IDS_ERROR(10117,"批量删除模型实例失败，ids -> {0}! "),
    GET_DATASOURCE_TABLE_LIST_FAILURE(10139,"获取数据源表列表失败！"),


    UDF_FUNCTION_NOT_EXIST(20001, "UDF 函数不存在"),
    UDF_FUNCTION_EXISTS(20002, "UDF函数已存在！"),
//    RESOURCE_EMPTY(20003, "resource file is empty"),
    RESOURCE_NOT_EXIST(20004, "资源文件不存在！"),
    RESOURCE_EXIST(20005, "资源文件已存在！"),
    RESOURCE_SUFFIX_NOT_SUPPORT_VIEW(20006, "资源文件后缀不支持在线查看！"),
    RESOURCE_SIZE_EXCEED_LIMIT(20007, "上传到资源文件大小超过限制！"),
    RESOURCE_SUFFIX_FORBID_CHANGE(20008, "资源文件后缀不允许被修改！"),
    UDF_RESOURCE_SUFFIX_NOT_JAR(20009, "UDF资源文件后缀必须为jar！"),
    HDFS_COPY_FAIL(20009, "HDFS复制 {0} -> {1} 失败！"),
    RESOURCE_FILE_EXIST(20010, "资源文件{0}已经存在于HDFS上，请先删除或者修改名称！"),
    RESOURCE_FILE_NOT_EXIST(20011, "资源文件{0}不在hdfs上！"),
    CAT_HDFS_FILE_ERROR(20012, "读取HDFS文件失败！"),
    CAT_COMPONENT_RESULT_FAIL(20013, "读取组件结果失败！"),


    USER_NO_OPERATION_PERM(30001, "用户没有操作权限！"),
    USER_NO_OPERATION_PROJECT_PERM(30002, "{0}用户没有{1}模型权限！"),


    PROCESS_INSTANCE_NOT_EXIST(50001, "模型实例不存在！"),
    PROCESS_INSTANCE_EXIST(50002, "模型定义已存在！"),
    PROCESS_DEFINE_NOT_EXIST(50003, "模型定义不存在！"),
    PROCESS_DEFINE_NOT_RELEASE(50004, "模型定义不在线！"),
    PROCESS_INSTANCE_ALREADY_CHANGED(50005, "模型实例的状态已存在！"),
    PROCESS_INSTANCE_STATE_OPERATION_ERROR(50006, "模型实例的状态不正确，无法执行操作！"),
    SUB_PROCESS_INSTANCE_NOT_EXIST(50007, "任务所属模型不存在！"),
    PROCESS_DEFINE_NOT_ALLOWED_EDIT(50008, "模型定义不允许被编辑！"),
    PROCESS_INSTANCE_EXECUTING_COMMAND(50009, "模型实例正在执行命令，请等待... "),
    PROCESS_INSTANCE_NOT_SUB_PROCESS_INSTANCE(50010, "模型实例不是一个子模型实例！"),
    TASK_INSTANCE_STATE_COUNT_ERROR(50011,"任务实例状态统计出错！"),
    COUNT_PROCESS_INSTANCE_STATE_ERROR(50012,"统计模型实例状态出错！"),
    COUNT_PROCESS_DEFINITION_USER_ERROR(50013,"统计模型定义用户出错！"),
    START_PROCESS_INSTANCE_ERROR(50014,"启动模型实例失败！"),
    EXECUTE_PROCESS_INSTANCE_ERROR(50015,"执行模型实例出错！"),
    CHECK_PROCESS_DEFINITION_ERROR(50016,"检验模型定义出错！"),
    QUERY_RECIPIENTS_AND_COPYERS_BY_PROCESS_DEFINITION_ERROR(50017,"通过模型定义查询收件人和抄送者出错！"),
    DATA_IS_NOT_VALID(50017,"数据不合法！"),
    DATA_IS_NULL(50018,"数据为空！"),
    PROCESS_NODE_HAS_CYCLE(50019,"模型节点不允许有闭环！"),
    PROCESS_NODE_S_PARAMETER_INVALID(50020,"模型节点参数不正确！"),
    PROCESS_DEFINE_STATE_ONLINE(50021, "模型定义已经上线！"),
    DELETE_PROCESS_DEFINE_BY_ID_ERROR(50022,"通过ID删除模型出错！"),
    SCHEDULE_CRON_STATE_ONLINE(50023,"调度状态已上线！"),
    DELETE_SCHEDULE_CRON_BY_ID_ERROR(50024,"通过ID删除调度出错！"),
    BATCH_DELETE_PROCESS_DEFINE_ERROR(50025,"批量删除模型定义出错！"),
    BATCH_DELETE_PROCESS_DEFINE_BY_IDS_ERROR(50026,"通过IDs批量删除模型定义出错！"),

    HDFS_NOT_STARTUP(60001,"hdfs配置开关未打开！"),
    HDFS_TERANT_RESOURCES_FILE_EXISTS(60002,"资源文件已存在，请先删除！"),
    HDFS_TERANT_UDFS_FILE_EXISTS(60003,"udf文件已存在，请先删除！"),

    /**
     * for monitor
     */
    QUERY_DATABASE_STATE_ERROR(70001,"查询数据库状态出错！"),
    QUERY_ZOOKEEPER_STATE_ERROR(70002,"查询zookeeper状态出错！"),



    CREATE_ACCESS_TOKEN_ERROR(70001,"创建访问令牌出错！"),
    GENERATE_TOKEN_ERROR(70002,"生成令牌出错！"),
    QUERY_ACCESSTOKEN_LIST_PAGING_ERROR(70003,"查询访问令牌分页列表出错！"),


    COMMAND_STATE_COUNT_ERROR(80001,"任务实例统计出错"),

    QUEUE_COUNT_ERROR(90001,"队列统计出错"),

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
