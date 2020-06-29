package com.bcht.data_manager.entity;

import java.util.Date;

public class ClouderaEntity {
    private Date xAxis;

    private Object yAxis1;

    private Object yAxis2;

    private String type;


    public Date getxAxis() {
        return xAxis;
    }

    public void setxAxis(Date xAxis) {
        this.xAxis = xAxis;
    }

    public Object getyAxis1() {
        return yAxis1;
    }

    public void setyAxis1(Object yAxis1) {
        this.yAxis1 = yAxis1;
    }

    public Object getyAxis2() {
        return yAxis2;
    }

    public void setyAxis2(Object yAxis2) {
        this.yAxis2 = yAxis2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
