package com.mrwind.order.entity;

import java.math.BigDecimal;

public class WechatTemplate {

	
	private ShopUser shop;
	private String tranNo;
	private BigDecimal money;
	private String executorName;

	public String getTranNo() {
		return tranNo;
	}
	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}
	public BigDecimal getMoney() {
		return money;
	}
	public void setMoney(BigDecimal money) {
		this.money = money;
	}
	public String getExecutorName() {
		return executorName;
	}
	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}
	public ShopUser getShop() {
		return shop;
	}
	public void setShop(ShopUser shop) {
		this.shop = shop;
	}
	
	
}
