package com.bcht.data_manager.enums;

/**
 * user type
 */
public enum DbType {
    HIVE("Hive", 1), HBASE("HBase", 2), HDFS("HDFS", 3);

    private String name;
    private int index;

    private DbType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}
