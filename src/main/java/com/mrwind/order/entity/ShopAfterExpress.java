package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by yfmacmini001 on 2017/3/6.
 */
public class ShopAfterExpress {
    private String shopId;
    private Set<String> expressNo;
    private BigDecimal totalPrice;
    public String getShopId() {
        return shopId;
    }
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public Set<String> getExpressNo() {
        return expressNo;
    }
    public void setExpressNo(Set<String> expressNo) {
        this.expressNo = expressNo;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
