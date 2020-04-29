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
    public static Connection getHiveConnection(DataSource dataSource) {
        return getHiveConnection(dataSource.getIp(), dataSource.getPort(), dataSource.getCategory1());
    }

    public static Connection getHiveConnection(String ip, int port, String database) {

        Connection connection = null;
        try{
            Class.forName(Constants.ORG_APACHE_HIVE_JDBC_HIVE_DRIVER);
            connection = DriverManager.getConnection("jdbc:hive2://" + ip + ":" + port + "/" + database);
        } catch (SQLException e) {
            logger.error("获取链接失败！请检查JDBC URL" + e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error("加载Hive驱动类失败，请确认jar包在classpath中" + e.getMessage());
        }
        return connection;
    }

    // DDL
    public static void createDatabase(DataSource dataSource) {
        Connection conn =  getHiveConnection(dataSource);
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("create database " + dataSource.getCategory1());
        } catch (SQLException e) {
            logger.error("创建数据库失败/或库已被创建，请检查库名" + e.getMessage());
        } finally{
            close(conn, stmt);
        }
    }

    public static void createTable(DataSource dataSource, String createSql) {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        try{
            stmt = connection.createStatement();
            stmt.execute(createSql);
        } catch (Exception e){
            logger.error("创建表失败，报错信息" + e.getMessage());
        } finally {
            close(connection, stmt);
        }
    }

    public static void addTableColumn(DataSource dataSource, String tableName, List<Map<String, String>> columns) {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        StringBuilder alterTableSql = new StringBuilder("alter table " +  tableName + " add column (");
        for(int i = 0; i < columns.size(); i ++) {
            Map<String, String> columnInfo = columns.get(i);
            alterTableSql.append(columnInfo.get("name") + " " + columnInfo.get("type") + " comment '" + columnInfo.get("comment") + "'");
        }
        alterTableSql.append(")");
        try {
            stmt = connection.createStatement();
            stmt.execute(alterTableSql.toString());
        } catch (SQLException e) {
            logger.error("添加字段失败" + e.getMessage());
        } finally{
          close(connection, stmt);
        }
    }

    public static void dropTable(DataSource dataSource, String tableName) {
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        try{
           stmt = connection.createStatement();
           stmt.execute("drop table " + tableName);
        } catch (SQLException e) {
            logger.error("删除表失败" + e.getMessage());
        } finally {
            close(connection, stmt);
        }
    }

    public static List<String> getDbTableList(DataSource dataSource) {
        List<String> result = new ArrayList<>();
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.createStatement();
            rs = stmt.executeQuery("show tables");
            while (rs.next()) { result.add(rs.getString(1)); }
        }catch (SQLException e) {
            logger.error("查询数据库表清单失败" + e.getMessage());
        }finally {
            close(connection, stmt, rs);
        }
        return result;

    }

    public static List<Map<String, String>> getTableColumnMapList(DataSource dataSource, String tableName) {
        List<Map<String, String>> columnList = new ArrayList<>();
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.createStatement();
            rs = stmt.executeQuery("desc " + tableName);
            while (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("name", rs.getString(1));
                m.put("type", rs.getString(2));
                m.put("comment", rs.getString(3));
                columnList.add(m);
            }
        }catch (SQLException e) {
            logger.error("查询表字段信息失败" + e.getMessage());
        }finally {
            close(connection, stmt, rs);
        }
        return columnList;
    }

    public static List<String> getTableColumnNameList(DataSource dataSource, String tableName) {
        List<String> columnList = new ArrayList<>();
        Connection connection = getHiveConnection(dataSource);
        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.createStatement();
            rs = stmt.executeQuery("desc " + tableName);
            while (rs.next()) {
                columnList.add(rs.getString(1));
            }
        }catch (SQLException e) {
            logger.error("查询表字段信息失败" + e.getMessage());
        }finally {
            close(connection, stmt, rs);
        }
        return columnList;
    }

    //DML
    public static List<String> downloadTableData(DataSource dataSource, String tableName, String condition) {
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

        StringBuilder sql = new StringBuilder("select * from " + tableName + " limit " + Constants.maxDownloadHiveLines);

        if(condition != null && org.apache.commons.lang3.StringUtils.isNotEmpty(condition)) {
            sql.append( " " + condition);
        }

        Statement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql.toString());

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
        } catch (SQLException e) {
            logger.error("查询表数据失败" + e.getMessage());
        } finally {
            close(connection, stmt, rs);
        }
        return result;

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
