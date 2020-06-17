package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HBaseUtils {
    public static final Logger logger = LoggerFactory.getLogger(HBaseUtils.class);

    // 获取HBase连接信息
    public static Connection getHBaseConnection(DataSource dataSource) throws IOException {
        return getHBaseConnection(dataSource.getIp(), dataSource.getPort());
    }

    public static Connection getHBaseConnection(String ip, int port) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set(Constants.HBASE_ZOOKEEPER_QUOEUM, ip);
        conf.set(Constants.HBASE_ZOOKEEPER_PROPERTY_CLIENTPORT, port + "");
        Connection conn = ConnectionFactory.createConnection(conf);
        return conn;
    }

    //DDL
    public static void createNameSpace(DataSource dataSource, String namespace) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        NamespaceDescriptor mkNameSpace = NamespaceDescriptor.create(namespace).build();
        admin.createNamespace(mkNameSpace);
        close(conn, admin);
    }

    public static void createNameSpace(DataSource dataSource) throws IOException {
        createNameSpace(dataSource, dataSource.getCategory1());
    }

    public static void dropNameSpace(DataSource dataSource, String namespace) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        admin.deleteNamespace(namespace);
        close(conn, admin);
    }

    public static void dropNameSpace(DataSource dataSource) throws IOException {
        dropNameSpace(dataSource, dataSource.getCategory1());
    }

    public static void createTable(DataSource dataSource, String tableName, String columnFamilies) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        TableDescriptorBuilder mk = TableDescriptorBuilder.newBuilder(TableName.valueOf(dataSource.getCategory1() + ":" + tableName));
        String[] columnFamilyArray = columnFamilies.split(",");
        List<ColumnFamilyDescriptor> columnFamilyDescriptors=new ArrayList<>();
        for(int i = 0;  i < columnFamilyArray.length; i ++) {
            ColumnFamilyDescriptorBuilder columnBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamilyArray[i]));
            columnFamilyDescriptors.add(columnBuilder.build());
        }
        mk.setColumnFamilies(columnFamilyDescriptors);
        admin.createTable(mk.build());
        close(conn, admin);
    }

    public static void disableTable(DataSource dataSource, String tableName) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        TableName tb = TableName.valueOf(tableName);
        if(admin.tableExists(tb)) {
            admin.disableTable(tb);
        }
        close(conn, admin);

    }

    public static void dropTable(DataSource dataSource, String tableName) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        TableName tb = TableName.valueOf(tableName);
        if(admin.tableExists(tb)) {
            admin.disableTable(tb);
            admin.deleteTable(tb);
        }
        close(conn, admin);

    }

    public static void addColumnFamily(DataSource dataSource, String tableName, String columnFamily) throws IOException {
        Connection conn = null;
        Admin admin = null;
            conn = getHBaseConnection(dataSource);
            admin = conn.getAdmin();
            TableName tb = TableName.valueOf(tableName);
            if(admin.tableExists(tb)) {
                ColumnFamilyDescriptorBuilder columnBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily));
                admin.addColumnFamily(tb, columnBuilder.build());
            }
            close(conn, admin);
    }

    public static List<String> getTableColumnFamilyList(DataSource dataSource, String tableName) throws IOException {
        List<String> list = new ArrayList<>();
        Connection conn = null;
        Table table = null;
            conn = getHBaseConnection(dataSource);
            Admin admin = conn.getAdmin();
            table = conn.getTable(TableName.valueOf(tableName));
            HTableDescriptor hTableDescriptor=table.getTableDescriptor();
            for(HColumnDescriptor fdescriptor : hTableDescriptor.getColumnFamilies()){
                list.add(fdescriptor.getNameAsString());
            }
            close(conn, null, table);
        return list;
    }

    // DML
    public static List<String> downloadTableData(DataSource dataSource, String tableName, int limit) throws IOException {
        List<String> resultList = new ArrayList<>();
        Connection connection =  null;
        Table table = null;
        connection = getHBaseConnection(dataSource);
        table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner resultScanner = table.getScanner(scan);
        if (limit == 0) { limit  = Constants.maxDownloadRecord; }
        int counter = 1;
        for(Result result : resultScanner) {
            if(counter <= limit) {
                resultList.add(result.toString());
            } else {
                break;
            }

        }
        close(connection, null, table);
        return  resultList;
    }

    public static List<Map<String, Object>> previewTableData(DataSource dataSource, String tableName) throws IOException {
        return null;
    }

    //关闭资源
    public static void close(Connection conn, Admin admin, Table table) throws IOException {
        if(table != null) {
            try{
                table.close();
            }catch (IOException e) {
                logger.error("关闭HBase Table失败" + e.getMessage());
            }
        }
        close(conn, admin);
    }

    public static void close(Connection conn, Admin admin) throws IOException {
        if(admin != null) {
            try{
                admin.close();
            }catch (IOException e) {
                logger.error("关闭HBase Admin失败" + e.getMessage());
            }
        }
        close(conn);
    }

    public static void close(Connection conn) throws IOException {
        if(conn != null) {
            try{
                conn.close();
            }catch (IOException e) {
                logger.error("关闭HBase 连接失败" + e.getMessage());
            }
        }
    }

}
