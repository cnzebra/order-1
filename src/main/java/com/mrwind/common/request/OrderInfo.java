package com.mrwind.common.request;

/**
 * Created by ycmac on 2017/4/20.
 */
public class OrderInfo {

    private String tel;
    private int weight;
    private String receiverName;    //收件人姓名
    private String receiverAddress; //收件人地址
    private String expressNo;       //真实运单号
    private String bindExpressNo;   //绑定第三方单号

    public OrderInfo() {
    }

    public OrderInfo(String tel, int weight, String receiverName, String receiverAddress, String expressNo, String bindExpressNo) {
        this.tel = tel;
        this.weight = weight;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.expressNo = expressNo;
        this.bindExpressNo = bindExpressNo;
    }

    public OrderInfo(String tel, String receiverName, String receiverAddress, String expressNo, String bindExpressNo) {
        this.tel = tel;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.expressNo = expressNo;
        this.bindExpressNo = bindExpressNo;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getBindExpressNo() {
        return bindExpressNo;
    }

    public void setBindExpressNo(String bindExpressNo) {
        this.bindExpressNo = bindExpressNo;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }
}
