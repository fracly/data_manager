package com.bcht.data_manager.enums;

/**
 * user type
 */
public enum UserType {
    /**
     * 0 admin user; 1 general user
     */
    ADMIN_USER("管理员", 0),
    GENERAL_USER("普通用户", 1);

    private String name;
    private int value;

    private UserType(String name, int value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
