package com.mrwind.common.request;

import com.mrwind.order.App;

/**
 * Created by ycmac on 2017/4/20.
 */
public class MissionShop {

    private Customer shop;                                  //商户信息
    private CustomerFence sender;                           //寄件人的信息
    private CustomerFence receiver;                           //寄件人的信息
    private String mode = App.ORDER_MODE_TODAY;            //类型
    private int weight;
    private int num;

    public Customer getShop() {
        return shop;
    }

    public void setShop(Customer shop) {
        this.shop = shop;
    }


    public CustomerFence getSender() {
        return sender;
    }

    public void setSender(CustomerFence sender) {
        this.sender = sender;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public CustomerFence getReceiver() {
        return receiver;
    }

    public void setReceiver(CustomerFence receiver) {
        this.receiver = receiver;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
