package com.mrwind.common.request;

import java.util.List;

/**
 * Created by ge-boox on 2017/4/21.
 */
public class ClaimOrder {
    private List<String> expressNo;
    private String executorUserId;
    private String name;
    private String tel;
    private Double lat;
    private Double lng;
    private String address;

    public List<String> getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(List<String> expressNo) {
        this.expressNo = expressNo;
    }

    public String getExecutorUserId() {
        return executorUserId;
    }

    public void setExecutorUserId(String executorUserId) {
        this.executorUserId = executorUserId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
