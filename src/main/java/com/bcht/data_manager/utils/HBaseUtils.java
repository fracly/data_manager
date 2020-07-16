package com.bcht.data_manager.utils;

import com.bcht.data_manager.consts.Constants;
import com.bcht.data_manager.entity.DataSource;
import com.bcht.data_manager.entity.Rule;
import com.bcht.data_manager.entity.UDPPacket;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.*;

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

    public static void createTable(DataSource dataSource, String tableName) throws IOException {
        Connection conn = null;
        Admin admin = null;
        conn = getHBaseConnection(dataSource);
        admin = conn.getAdmin();
        TableDescriptorBuilder mk = TableDescriptorBuilder.newBuilder(TableName.valueOf(dataSource.getCategory1() + ":" + tableName));
        ColumnFamilyDescriptorBuilder columnBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(Constants.HBASE_DEFAULT_COLUMN_FAMILY));
        mk.setColumnFamily(columnBuilder.build());
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
        Connection conn = getHBaseConnection(dataSource);
        Admin admin = conn.getAdmin();
        TableName tb = TableName.valueOf(dataSource.getCategory1() + ":" + tableName);
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
        Connection conn = getHBaseConnection(dataSource);
        Table table = conn.getTable(TableName.valueOf(dataSource.getCategory1() + ":" + tableName));
        HTableDescriptor hTableDescriptor=table.getTableDescriptor();
        for(HColumnDescriptor fdescriptor : hTableDescriptor.getColumnFamilies()){
            list.add(fdescriptor.getNameAsString());
        }
        close(conn, null, table);
        return list;
    }

    // DML
    public static void insertUDPPacket(DataSource dataSource, String tableName, Rule rule, byte[] data) throws IOException {
        Connection connection = getHBaseConnection(dataSource);
        Table table = connection.getTable(TableName.valueOf(dataSource.getCategory1() + ":" + tableName));
        // rowkey = 时间戳反转+ 数据长度 + 偏移量 + 偏移长度 + 功能号
        Date now = new Date();
        Long timestamp = now.getTime();
        String timeStamp = String.valueOf(timestamp);
        String rowKey = timeStamp.substring(timeStamp.length()-1) +  "-" + timestamp + "-" + rule.getLength() + "-" + rule.getOffset() + "-" + rule.getValue();
        Put put = new Put(rowKey.getBytes());
        put.addColumn(Constants.HBASE_DEFAULT_COLUMN_FAMILY.getBytes(), "length".getBytes(), String.valueOf(rule.getLength()).getBytes()) ;
        put.addColumn(Constants.HBASE_DEFAULT_COLUMN_FAMILY.getBytes(), "offset".getBytes(), String.valueOf(rule.getOffset()).getBytes() ) ;
        put.addColumn(Constants.HBASE_DEFAULT_COLUMN_FAMILY.getBytes(), "data".getBytes(), data) ;
        put.addColumn(Constants.HBASE_DEFAULT_COLUMN_FAMILY.getBytes(), "value".getBytes(), rule.getValue().getBytes());
        table.put(put);
    }

    public static List<UDPPacket> queryUDPPacket(DataSource dataSource, String tableName, Long minStamp, Long maxStamp) throws IOException {
        Connection connection = getHBaseConnection(dataSource);
        List list = new ArrayList();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        if (minStamp != null && maxStamp != null) {
            scan.setTimeRange(minStamp, maxStamp);
        }
        ResultScanner resultScanner = table.getScanner(scan);
        for(Result row : resultScanner){
            UDPPacket packet = new UDPPacket();
            packet.setData(row.getValue(Constants.HBASE_DEFAULT_COLUMN_FAMILY.getBytes(), Constants.HBASE_DATA_COLUMN.getBytes()));
            list.add(packet);
        }
        return list;
    }

    public static long countTableRecord(DataSource dataSource, String tableName, Long minStamp, Long maxStamp) throws IOException {
        long total = 0;
        Connection connection = getHBaseConnection(dataSource);
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        if(minStamp != null && maxStamp != null) {
            scan.setTimeRange(minStamp, maxStamp);
        }
        ResultScanner resultScanner = table.getScanner(scan);
        for(Result row : resultScanner){
            total ++;
        }
        return total;
    }

    public static List<String> downloadTableData(DataSource dataSource, String tableName, int limit) throws IOException {
        List<String> resultList = new ArrayList<>();
        Connection connection =  getHBaseConnection(dataSource);
        Table table = connection.getTable(TableName.valueOf(tableName));
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
