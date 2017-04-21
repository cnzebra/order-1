package com.mrwind.common.request;

import com.mrwind.order.entity.ShopUser;
import com.mrwind.order.entity.User;

import java.util.Date;

/**
 * @Author admin
 * @Date 2017/4/14
 */
public class CallNew {

    private Date dueTime;  //预约时间

    private Integer num;    //数量

    private Integer weight; //单件重量

    private String remark;  //备注

    private String type; //客户类型VIP()|GENERAL(普通用户,默认值)

    private ShopUser shop;

    private String mode;    //模式

    private User sender; //寄件人信息

    private PersonAddressOrder receiver; //收件人信息


    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public PersonAddressOrder getReceiver() {
        return receiver;
    }

    public void setReceiver(PersonAddressOrder receiver) {
        this.receiver = receiver;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public ShopUser getShop() {
        return shop;
    }

    public void setShop(ShopUser shop) {
        this.shop = shop;
    }
}
