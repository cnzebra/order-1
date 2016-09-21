package com.mrwind.category.entity;

import java.math.BigDecimal;

public class RaisePrice {

    private Double a;
    private Double b;
    private BigDecimal priceDelta;
    
    public Double getA() {
        return a;
    }
    public void setA(Double a) {
        this.a = a;
    }
    public Double getB() {
        return b;
    }
    public void setB(Double b) {
        this.b = b;
    }
    public BigDecimal getPriceDelta() {
        return priceDelta;
    }
    public void setPriceDelta(BigDecimal priceDelta) {
        this.priceDelta = priceDelta;
    }

}
