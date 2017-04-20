package com.mrwind.common.request;

/**
 * 寄收件人类型
 * Created by ycmac on 2017/4/20.
 */
public enum ShopAddressType {
    SEND(1),
    RECEIVER(2);

    public int value;

    ShopAddressType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        System.out.println(ShopAddressType.SEND.value);    //    1
        System.out.println(ShopAddressType.SEND.getValue());    //    1
        System.out.println(ShopAddressType.RECEIVER.value);    //   2
    }
}
