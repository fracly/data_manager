package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiveUtils {
    public static final Logger logger = LoggerFactory.getLogger(HiveUtils.class);

    // 获取链接, 所以一切操作，都需要指定数据源
    public static Connection getHiveConnection(DataSource dataSource) throws SQLException, ClassNotFoundException {
        return getHiveConnection(dataSource.getIp(), dataSource.getPort(), dataSource.getCategory1());
    }

    public static Connection getHiveConnection(String ip, int port, String database) throws SQLException, ClassNotFoundException {

        Connection connection = null;
        Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
        connection = DriverManager.getConnection("jdbc:hive2://" + ip + ":" + port + "/" + database);
        return connection;
    }

    // DDL
    public static void createDatabase(DataSource dataSource, String database) throws SQLException, ClassNotFoundException {
        Connection conn =  getHiveConnection(dataSource);
        Statement stmt = conn.createStatement();
        stmt.execute("create database " + database);
        close(conn, stmt);
    }

    public static void createDatabase(DataSource dataSource) throws SQLException, ClassNotFoundException {
        createDatabase(dataSource, dataSource.getCategory1());
    }

    public static void dropDatabase(DataSource dataSource, String database) throws SQLException, ClassNotFoundException {
        Connection conn =  getHiveConnection(dataSource);
        Statement stmt = conn.createStatement();
        stmt.execute("drop database " + database);
        close(conn, stmt);
    }

    public static void dropDatabase(DataSource dataSource) throws SQLException, ClassNotFoundException {
        dropDatabase(dataSource, dataSource.getCategory1());
    }

    public static void createTable(DataSource dataSource, String createSql) throws SQLException, ClassNotFoundException {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = connection.createStatement();
        stmt.execute(createSql);
        close(connection, stmt);
    }

    public static void addTableColumn(DataSource dataSource, String tableName, List<Map> columns)  throws SQLException, ClassNotFoundException{
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = connection.createStatement();
        StringBuilder alterTableSql = new StringBuilder("alter table " +  tableName + " add columns (");
        StringUtils.appendColumns(alterTableSql, columns);
        stmt.execute(alterTableSql.toString());
        close(connection, stmt);
    }

    public static void dropTable(DataSource dataSource, String tableName) throws SQLException, ClassNotFoundException {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = connection.createStatement();
        stmt.execute("drop table " + tableName);
        close(connection, stmt);
    }

    public static List<String> getDbTableList(DataSource dataSource) throws SQLException, ClassNotFoundException {
        List<String> result = new ArrayList<>();
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("show tables");
        while (rs.next()) { result.add(rs.getString(1)); }
        close(connection, stmt, rs);
        return result;

    }

    public static List<Map<String, String>> getTableColumnMapList(DataSource dataSource, String tableName) throws SQLException, ClassNotFoundException {
        List<Map<String, String>> columnList = new ArrayList<>();
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("desc " + tableName);
        while (rs.next()) {
            Map<String, String> m = new HashMap<>();
            m.put("name", rs.getString(1));
            m.put("type", rs.getString(2));
            m.put("comment", rs.getString(3));
            columnList.add(m);
        }
        close(connection, stmt, rs);
        return columnList;
    }

    public static List<String> getTableColumnNameList(DataSource dataSource, String tableName) throws SQLException, ClassNotFoundException {
        List<String> columnList = new ArrayList<>();
        List<Map<String, String>> mapList = getTableColumnMapList(dataSource, tableName);
        for(Map<String, String> map : mapList) {
            columnList.add(map.get("name"));
        }
        return columnList;
    }



    //DML
    public static List<String> downloadTableData(DataSource dataSource, String tableName, String condition, int limit) throws SQLException, ClassNotFoundException {
        List<String> result = new ArrayList<>();

        Connection connection = getHiveConnection(dataSource);
        List<String> columnList = getTableColumnNameList(dataSource, tableName);

        StringBuilder firstLine = new StringBuilder();
        for(int j = 0; j < columnList.size(); j ++) {
            firstLine.append(columnList.get(j));
            if( j == (columnList.size() - 1))
            if(j != columnList.size()) {
                firstLine.append("\n");
            } else {
                firstLine.append(",");
            }
        }
        result.add(firstLine.toString());

        StringBuilder sql = new StringBuilder();
        if (limit == 0) {
            sql.append("select * from " + tableName + " limit " + Constants.maxDownloadRecord);
        } else {
            sql.append("select * from " + tableName + " limit " + limit);
        };

        if(condition != null && org.apache.commons.lang3.StringUtils.isNotEmpty(condition)) {
            sql.append( " " + condition);
        }

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql.toString());
        while(rs.next()) {
            StringBuilder row = new StringBuilder();
            for(int i = 0; i < columnList.size(); i ++) {
                row.append(rs.getString(columnList.get(i)));
                if(i == (columnList.size() - 1)) {
                    row.append("\n");
                } else {
                    row.append(",");
                }
            }
            result.add(row.toString());
        }
        close(connection, stmt, rs);
        return result;

    }

    public static List<String> previewTableData(DataSource dataSource, String tableName) throws SQLException, ClassNotFoundException {
        return downloadTableData(dataSource,tableName, null, Constants.maxPreviewRecord);
    }
    // 关闭资源
    private static void close(Connection connection, Statement stmt, ResultSet rs) {
        if(rs != null) {
            try{
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(connection, stmt);

    }

    private static void close(Connection connection, Statement stmt) {
        if(stmt != null) {
            try{
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(connection);

    }

    private static void close(Connection connection) {
        if(connection != null) {
            try{
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
