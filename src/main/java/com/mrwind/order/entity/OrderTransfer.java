package com.mrwind.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document
public class OrderTransfer {
	
	@Id
	private String id;
	private String expressNo;			//运单号
	private String excutorId;			//当前执行人
	private Double lat;					//纬度
	private Double lng;					//经度
	private String address;				//当前执行地址
	private String conveyanceTool;		//运输工具
	private Date operaTime;				//当前执行时间
	private String preExcutorId;
	private Double preLat;
	private Double preLng;
	private String preAddress;
	private String status;				//对于当前人的状态；直取、取转、转取、转派、直派
	private BigDecimal distance;		//单次的里程
	private BigDecimal totalDistance;	//总共的里程
	private BigDecimal weight;			//单次的重量
	private BigDecimal totalWeight;		//总共的重量

	public OrderTransfer() {
	}

	public OrderTransfer(String expressNo, String excutorId, Double lat, Double lng, String address, Date operaTime, String preExcutorId, Double preLat, Double preLng, String preAddress, String status) {
		this.expressNo = expressNo;
		this.excutorId = excutorId;
		this.lat = lat;
		this.lng = lng;
		this.address = address;
		this.operaTime = operaTime;
		this.preExcutorId = preExcutorId;
		this.preLat = preLat;
		this.preLng = preLng;
		this.preAddress = preAddress;
		this.status = status;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public String getExcutorId() {
		return excutorId;
	}

	public void setExcutorId(String excutorId) {
		this.excutorId = excutorId;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getOperaTime() {
		return operaTime;
	}

	public void setOperaTime(Date operaTime) {
		this.operaTime = operaTime;
	}

	public String getPreExcutorId() {
		return preExcutorId;
	}

	public void setPreExcutorId(String preExcutorId) {
		this.preExcutorId = preExcutorId;
	}

	public Double getPreLat() {
		return preLat;
	}

	public void setPreLat(Double preLat) {
		this.preLat = preLat;
	}

	public Double getPreLng() {
		return preLng;
	}

	public void setPreLng(Double preLng) {
		this.preLng = preLng;
	}

	public String getPreAddress() {
		return preAddress;
	}

	public void setPreAddress(String preAddress) {
		this.preAddress = preAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getDistance() {
		return distance;
	}

	public void setDistance(BigDecimal distance) {
		this.distance = distance;
	}

	public BigDecimal getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(BigDecimal totalDistance) {
		this.totalDistance = totalDistance;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}

	public String getConveyanceTool() {
		return conveyanceTool;
	}

	public void setConveyanceTool(String conveyanceTool) {
		this.conveyanceTool = conveyanceTool;
	}
}
