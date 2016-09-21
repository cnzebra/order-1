package com.mrwind.category.entity;

import java.math.BigDecimal;

public class DiscountPlan {
    
    private BigDecimal discount;
    private BigDecimal rechargeThan;
    
    public BigDecimal getDiscount() {
        return discount;
    }
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    public BigDecimal getRechargeThan() {
        return rechargeThan;
    }
    public void setRechargeThan(BigDecimal rechargeThan) {
        this.rechargeThan = rechargeThan;
    }
    
}
