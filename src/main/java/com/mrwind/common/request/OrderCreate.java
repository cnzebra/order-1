package com.mrwind.common.request;

/**
 * Created by ycmac on 2017/4/20.
 */
public class OrderCreate {

    private Customer shop;          //商户信息
    private ExecutorInfo executor;  //当前执行人的信息
    private CustomerFence sender;   //寄件人的信息
    private CustomerFence receiver;   //寄件人的信息
    private OrderInfo orderInfo;    //订单基本信息
    private Category category;
    private String mode;            //类型
    private String bindExpressNo;   //第三方单号

    public OrderCreate() {
    }

    public OrderCreate(Customer shop, ExecutorInfo executor, CustomerFence sender, CustomerFence receiver, OrderInfo orderInfo, Category category, String mode) {
        this.shop = shop;
        this.executor = executor;
        this.sender = sender;
        this.receiver = receiver;
        this.orderInfo = orderInfo;
        this.category = category;
        this.mode = mode;
    }

    public Customer getShop() {
        return shop;
    }

    public void setShop(Customer shop) {
        this.shop = shop;
    }

    public ExecutorInfo getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorInfo executor) {
        this.executor = executor;
    }

    public CustomerFence getSender() {
        return sender;
    }

    public void setSender(CustomerFence sender) {
        this.sender = sender;
    }

    public CustomerFence getReceiver() {
        return receiver;
    }

    public void setReceiver(CustomerFence receiver) {
        this.receiver = receiver;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBindExpressNo() {
        return bindExpressNo;
    }

    public void setBindExpressNo(String bindExpressNo) {
        this.bindExpressNo = bindExpressNo;
    }
}
