package com.mrwind.common.request;

/**
 * Created by ycmac on 2017/4/20.
 */
public class Category {
    private int weight;
    private int distance;
    private String shopId;
    private String expressNo;

    public Category() {
    }

    public Category(int weight, int distance, String shopId) {
        this.weight = weight;
        this.distance = distance;
        this.shopId = shopId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }
}
