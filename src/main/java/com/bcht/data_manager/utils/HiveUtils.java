package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiveUtils {
    public static Connection getHiveConnection(String ip, int port, String category1) {
        Connection connection = null;
        try {
            Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
            connection = DriverManager.getConnection("jdbc:hive2://" + ip + ":" + port + "/" + category1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection getHiveConnection(DataSource dataSource) {
       return getHiveConnection(dataSource.getIp(), dataSource.getPort(), dataSource.getCategory1());
    }

    public static List<String> getDbTables(Connection conn) {
        List<String> tableList = new ArrayList<>();
        if(conn != null) {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery("show tables");
                while(rs.next()) {
                    tableList.add(rs.getString(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return tableList;
    }

    public static List<String> getDbTables(String ip, int port, String category1) {
        Connection conn = getHiveConnection(ip, port, category1);
        return getDbTables(conn);
    }

    public static List<Map<String, String>> getTableColumns(String ip, int port, String category1, String table) {
        List<Map<String, String>> columnList = new ArrayList<>();
        Connection conn = getHiveConnection(ip, port, category1);
        if(conn != null) {
            try{
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("desc " + table);

                while (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    String name = rs.getString(1);
                    String type1 = rs.getString(2);
                    String comment = rs.getString(3);
                    m.put("name",name);
                    m.put("type",type1);
                    m.put("comment",comment);
                    columnList.add(m);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return columnList;
    }


    public static boolean createTable(DataSource dataSource, String createSql) {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        try{
            stmt = connection.createStatement();
            stmt.execute("use " + dataSource.getCategory1());
            stmt.execute(createSql);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
