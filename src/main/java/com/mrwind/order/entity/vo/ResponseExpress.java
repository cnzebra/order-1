package com.mrwind.order.entity.vo;

import com.mrwind.order.entity.Category;
import com.mrwind.order.entity.ResponseCommet;
import com.mrwind.order.entity.ShopUser;
import com.mrwind.order.entity.User;

import java.util.Date;
import java.util.List;

/**
 * Created by ycmac on 2017/4/23.
 */
public class ResponseExpress {

    private Date dueTime;
    private String expressNo;
    private String bindExpressNo;
    private String status;
    private ShopUser shop;
    private User sender;
    private User receiver;
    private Category category;
    private List<ResponseCommet> commetList;
    private Date updateTime;

    public ResponseExpress() {
    }

    public ResponseExpress(Date dueTime, String expressNo,String bindExpressNo, String status, ShopUser shop, User sender, User receiver, Category category) {
        this.dueTime = dueTime;
        this.expressNo = expressNo;
        this.bindExpressNo = bindExpressNo;
        this.status = status;
        this.shop = shop;
        this.sender = sender;
        this.receiver = receiver;
        this.category = category;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ShopUser getShop() {
        return shop;
    }

    public void setShop(ShopUser shop) {
        this.shop = shop;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ResponseCommet> getCommetList() {
        return commetList;
    }

    public void setCommetList(List<ResponseCommet> commetList) {
        this.commetList = commetList;
    }

    public String getBindExpressNo() {
        return bindExpressNo;
    }

    public void setBindExpressNo(String bindExpressNo) {
        this.bindExpressNo = bindExpressNo;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
