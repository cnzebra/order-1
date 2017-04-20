package com.mrwind.common.request;


/**
 * Created by ycmac on 2017/4/20.
 */
public class CustomerFence extends Customer {

    private CommonBean fence;//商户所属围栏

    public CustomerFence(String name, Double lng, Double lat, String address, String tel) {
        super(name, lng, lat, address, tel);
    }

    public CustomerFence() {
    }

    public CommonBean getFence() {
        return fence;
    }

    public void setFence(CommonBean fence) {
        this.fence = fence;
    }
}
