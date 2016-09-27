package com.mrwind.order.entity;

import org.springframework.data.annotation.Id;

public class ShopReceiver {

	@Id
	private String id;
	private String name;
	private String tel;
	private String addr;
	private Double lng;
	private Double lat;
	private Fence fence;
	private Shop shopInfo;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Fence getFence() {
		return fence;
	}
	public void setFence(Fence fence) {
		this.fence = fence;
	}
	public Shop getShopInfo() {
		return shopInfo;
	}
	public void setShopInfo(Shop shopInfo) {
		this.shopInfo = shopInfo;
	}
}
