package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;

public class SqoopUtils {
    public static String importRDBSToHive(int type, String ip, int port, String database, String table, String username, String password,
                                          String targetDB, String targetTable, boolean overwrite) {
        StringBuilder sb = new StringBuilder();

        sb.append("sqoop import ");
        if(type == Constants.MYSQL) {
            sb.append(" --connect jdbc:mysql://" + ip + ":" + port + "/" + database);
        } else if(type == Constants.ORACLE) {
            sb.append(" --connect jdbc:oracle:thin://" + ip + ":" + port + "/" + database);
        } else if(type == Constants.SQLSERVER) {
            sb.append(" --connect jdbc:sqlserver://" + ip + ":" + port + "/" + database);
        } else if(type == Constants.DB2) {
            sb.append(" --connect jdbc:db2://" + ip + ":" + port + "/" + database);
        }
        if(overwrite) {
            sb.append(" --hive-overwrite");
        }
        sb.append(" --username " + username);
        sb.append(" --password " + password);
        sb.append(" --table " + table);
        sb.append(" --hive-import ");
        sb.append(" --hive-database " + targetDB);
        sb.append(" --hive-table " + targetTable);
        sb.append(" --fields-terminated-by |");
        sb.append(" --hive-delims-replacement '#'");
        sb.append(" -m 1");
        return sb.toString();
    }
}
