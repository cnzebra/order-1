package com.mrwind.category.entity;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CategoryAddition {
    
    private String id;
    private String name;
    private BigDecimal price;
    private String picturePath;
    private Boolean delFlag = Boolean.FALSE;
    
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
   
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Boolean getDelFlag() {
        return delFlag;
    }
    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }
    public String getPicturePath() {
        return picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

}
