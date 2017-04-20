package com.mrwind.common.request;

/**
 * Created by ycmac on 2017/4/20.
 */
public class Customer {

    private String id;
    private String name;
    private String tel;
    private String address;
    private Double lat;
    private Double lng;

    public Customer() {
    }

    public Customer(String name, Double lng, Double lat, String address, String tel) {
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.address = address;
        this.tel = tel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
