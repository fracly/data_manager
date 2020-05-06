package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;

public class SqoopUtils {
    public static String importRDBSToHive(String type, String ip, int port, String database, String table, String username, String password) {
        StringBuilder sb = new StringBuilder();

        sb.append("sqoop import ");
        if(type.equals(Constants.MYSQL)) {
            sb.append(" --connect jdbc:mysql://" + ip + ":" + port + "/" + database);
        } else if(type.equals(Constants.ORACLE)) {
            sb.append(" --connect jdbc:oracle:thin://" + ip + ":" + port + "/" + database);
        } else if(type.equals(Constants.SQLSERVER)) {
            sb.append(" --connect jdbc:sqlserver://" + ip + ":" + port + "/" + database);
        } else if(type.equals(Constants.DB2)) {
            sb.append(" --connect jdbc:db2://" + ip + ":" + port + "/" + database);
        }
        sb.append(" --username " + username);
        sb.append(" --password " + password);
        sb.append(" --table " + table);
        sb.append(" --hive-import ");

        return sb.toString();
    }
}
