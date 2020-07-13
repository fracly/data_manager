package com.bcht.data_manager.entity;

public class Rule {

    private String name;

    // 功能号的偏移量（单位: 字节）
    private int offset;

    // 功能号的长度 （单位: 字节）
    private int length;

    // 功能号的值 （十六进制的字符串）
    private String value;

    // 目标数据ID
    private int target;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
