package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.List;

public class Category {

	private String id;
	private Double weight;
	private BigDecimal weightPrice;
	private Double distance;
	private BigDecimal distancePrice;
	private Double estimatedValue;
	private BigDecimal insuredRates;
	private List<ProtectType> protectTypes;
	private BigDecimal protectPrice;
	private ServiceType serviceType;
	private BigDecimal servicePrice;
	private BigDecimal totalPrice;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public BigDecimal getWeightPrice() {
		return weightPrice;
	}

	public void setWeightPrice(BigDecimal weightPrice) {
		this.weightPrice = weightPrice;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public BigDecimal getDistancePrice() {
		return distancePrice;
	}

	public void setDistancePrice(BigDecimal distancePrice) {
		this.distancePrice = distancePrice;
	}

	public Double getEstimatedValue() {
		return estimatedValue;
	}

	public void setEstimatedValue(Double estimatedValue) {
		this.estimatedValue = estimatedValue;
	}

	public BigDecimal getInsuredRates() {
		return insuredRates;
	}

	public void setInsuredRates(BigDecimal insuredRates) {
		this.insuredRates = insuredRates;
	}

	public BigDecimal getProtectPrice() {
		return protectPrice;
	}

	public void setProtectPrice(BigDecimal protectPrice) {
		this.protectPrice = protectPrice;
	}

	public BigDecimal getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(BigDecimal servicePrice) {
		this.servicePrice = servicePrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public List<ProtectType> getProtectTypes() {
		return protectTypes;
	}

	public void setProtectTypes(List<ProtectType> protectTypes) {
		this.protectTypes = protectTypes;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
}
