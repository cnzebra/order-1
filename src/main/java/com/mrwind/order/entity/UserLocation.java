package com.mrwind.order.entity;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;

public class UserLocation implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -413821825072396537L;
	@Id
	private String id;
	private String name;
	@Indexed
	private String tel;
	private String address;
	private Double lng;
	private Double lat;
	private String avatar;
	private Fence fence;

	@GeoSpatialIndexed
	@JSONField(serialize = false)
	protected Double[] location;

	public UserLocation() {
	}

	public UserLocation(String id, String name, String tel, String address, Double lng, Double lat) {
		this.id = id;
		this.name = name;
		this.tel = tel;
		this.address = address;
		this.lng = lng;
		this.lat = lat;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Fence getFence() {
		return fence;
	}

	public void setFence(Fence fence) {
		this.fence = fence;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Double[] getLocation() {
		return location;
	}

	public void setLocation(Double[] location) {
		this.location = location;
	}
}
