package com.mrwind.category.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Category {

    @Id
    private String id;
    private String category;
    private String name;
    private Distance distance;
    private Date updateTime;
    private BigDecimal bottomPrice;
    private Double bottomPriceWeight;
    private BigDecimal insuredRate;
    private Double bottomPriceVolume;
    private Date createTime;
    private List<DiscountPlan> discountPlan;//折扣方案
    private List<RaisePrice> weighLevel;//重量加价
    private List<RaisePrice> volumeLevel;//体积加价
    private List<String> shops;//绑定商户
    private List<CategoryAddition> addition;//特殊要求
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Distance getDistance() {
        return distance;
    }
    public void setDistance(Distance distance) {
        this.distance = distance;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public BigDecimal getBottomPrice() {
        return bottomPrice;
    }
    public void setBottomPrice(BigDecimal bottomPrice) {
        this.bottomPrice = bottomPrice;
    }
    public Double getBottomPriceWeight() {
        return bottomPriceWeight;
    }
    public void setBottomPriceWeight(Double bottomPriceWeight) {
        this.bottomPriceWeight = bottomPriceWeight;
    }
    public BigDecimal getInsuredRate() {
        return insuredRate;
    }
    public void setInsuredRate(BigDecimal insuredRate) {
        this.insuredRate = insuredRate;
    }
    public Double getBottomPriceVolume() {
        return bottomPriceVolume;
    }
    public void setBottomPriceVolume(Double bottomPriceVolume) {
        this.bottomPriceVolume = bottomPriceVolume;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public List<DiscountPlan> getDiscountPlan() {
        return discountPlan;
    }
    public void setDiscountPlan(List<DiscountPlan> discountPlan) {
        this.discountPlan = discountPlan;
    }
    public List<RaisePrice> getWeighLevel() {
        return weighLevel;
    }
    public void setWeighLevel(List<RaisePrice> weighLevel) {
        this.weighLevel = weighLevel;
    }
    public List<RaisePrice> getVolumeLevel() {
        return volumeLevel;
    }
    public void setVolumeLevel(List<RaisePrice> volumeLevel) {
        this.volumeLevel = volumeLevel;
    }
    public List<String> getShops() {
        return shops;
    }
    public void setShops(List<String> shops) {
        this.shops = shops;
    }
    public List<CategoryAddition> getAddition() {
        return addition;
    }
    public void setAddition(List<CategoryAddition> addition) {
        this.addition = addition;
    }
    
}
