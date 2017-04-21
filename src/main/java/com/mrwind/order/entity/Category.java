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
	private List<ProtectType> protectTypeVOs;
	private BigDecimal protectPrice;
	private ServiceType serviceType;
	private BigDecimal servicePrice;
	private BigDecimal totalPrice;
	private User serviceUser;
	private BigDecimal artificialPrice;

	public Category() {
	}

	public Category(Double weight) {
		this.weight = weight;
	}

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

	public List<ProtectType> getProtectTypeVOs() {
		return protectTypeVOs;
	}

	public void setProtectTypeVOs(List<ProtectType> protectTypeVOs) {
		this.protectTypeVOs = protectTypeVOs;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public User getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(User serviceUser) {
		this.serviceUser = serviceUser;
	}

	public BigDecimal getArtificialPrice() {
		return artificialPrice;
	}

	public void setArtificialPrice(BigDecimal artificialPrice) {
		this.artificialPrice = artificialPrice;
	}
}
