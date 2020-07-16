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

    /** zookeeper **/
    public static final String HBASE_ZOOKEEPER_QUOEUM = "hbase.zookeeper.quorum";
    public static final String HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT = "hbase.zookeeper.property.clientPort";

    /**
     * session user
     */
    public static final String SESSION_USER = "session.user";

    public static final String SESSION_ID = "sessionId";


    /**
     * session timeout
     */
    public static final int SESSION_TIME_OUT = 7200;

    /**
     * uploadFileSize
     */
    public static final int maxFileSize = 1024 * 1024 * 1024;

    /**
     * max download hive lines
     */
    public static final int maxDownloadRecord = 200000;

    /**
     * max preview hive lines
     */
    public static final int maxPreviewRecord = 10;

    /**
     * driver
     */
    public static final String ORG_APACHE_HIVE_JDBC_HIVE_DRIVER = "org.apache.hive.jdbc.HiveDriver";

    public static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

    public static final String COM_ORACLE_JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

    public static final String COM_SQLSERVER_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static final String COM_DB2_JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driver";


    /**
     * create hive table method
     */
    public static final Integer CREATE_TABLE_METHOD_OF_CREATE_SQL = 1;

    public static final Integer CREATE_TABLE_METHOD_OF_COLUMN_COMPOSE = 2;


    /**
     * external api data interface
     */
    public static final String CLOUDEAR_HDFS_NAMENODE_URL = "cloudera.hdfs.namenode.url";

    /**
     * data.basedir.path
     */
    public static final String DATA_BASEDIR_PATH = "data.basedir.path";

    /**
     * data.download.basedir.path
     */
    public static final String DATA_DOWNLOAD_BASEDIR_PATH = "data.download.basedir.path";

    /**
     * data.upload.basedir.path
     */
    public static final String DATA_UPLOAD_BASEDIR_PATH = "data.upload.basedir.path";

    /**
     * date format of yyyy-MM-dd HH:mm:ss
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * date format of yyyyMMddHHmmss
     */
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * collection resource type
     */
    public static final Integer MYSQL = 1;

    public static final Integer ORACLE = 2;

    public static final Integer SQLSERVER = 3;

    public static final Integer DB2 = 4;

    public static final Integer FILE = 9;

    /**
     * job status
     */
    public static final Integer RUNNING = 2;

    public static final Integer FINISHED = 1;

    public static final Integer KILLED = 9;

    public static final Integer FAILED = -1;

    public static final Integer INITIAL = 0;

    /**
     * job type
     */
    public static final Integer DB2HIVE = 1;

    public static final Integer FILE2HIVE = 2;

    public static final String ADMIN = "admin";

    /**
     * Hbase
     */
    public static final String HBASE_DEFAULT_COLUMN_FAMILY = "default_column_family";

    public static final String HBASE_DATA_COLUMN = "data";
}
