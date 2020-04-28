package com.bcht.data_manager.consts;

/**
 * Constants class
 */
public final class Constants {
    /**
     * status
     */
    public static final String STATUS = "status";

    /**
     * message
     */
    public static final String MSG = "msg";


    /** data source **/

    public static final String SPRING_DATASOURCE_URL = "spring.datasource.url";

    public static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";

    public static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";

    public static final String SPRING_DATASOURCE_DRIVER_CLASS_NAME = "spring.datasource.driver-class-name";

    public static final String SPRING_DATASOURCE_VALIDATION_QUERY = "";

    public static final String DEVELOPMENT = "";

    /**
     * session user
     */
    public static final String SESSION_USER = "session.user";

    public static final String SESSION_ID = "sessionId";


    /**
     * session timeout
     */
    public static final int SESSION_TIME_OUT = 7200;
    public static final int maxFileSize = 1024 * 1024 * 1024;

    /**
     * driver
     */
    public static final String ORG_APACHE_HIVE_JDBC_HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    /**
     * create hive table method
     */
    public static final Integer CREATE_TABLE_METHOD_OF_CREATE_SQL = 1;

    public static final Integer CREATE_TABLE_METHOD_OF_COLUMN_COMPOSE = 2;


    /**
     * external api data interface
     */
    public static final String CLOUDEAR_HDFS_NAMENODE_URL = "cloudera.hdfs.namenode.url";
}
