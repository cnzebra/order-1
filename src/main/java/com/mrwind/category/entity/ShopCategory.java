package com.mrwind.category.entity;

import java.util.List;

import org.springframework.data.annotation.Id;

public class ShopCategory {
    
    @Id
    private String id;
    
    private String shopId;
    
    private List<String> categorys;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public List<String> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<String> categorys) {
        this.categorys = categorys;
    }

}
