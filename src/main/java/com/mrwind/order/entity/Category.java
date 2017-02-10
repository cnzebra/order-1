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
	private List<ServiceType> serviceTypes;
	private BigDecimal servicePrice;
	private BigDecimal totalPrice;

	public class ProtectType {
		private String name;
		private BigDecimal price;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
	}
	
	public class ServiceType{
	    private String name;
	    private BigDecimal price;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
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

	public List<ProtectType> getProtectTypes() {
		return protectTypes;
	}

	public void setProtectTypes(List<ProtectType> protectTypes) {
		this.protectTypes = protectTypes;
	}

	public BigDecimal getProtectPrice() {
		return protectPrice;
	}

	public void setProtectPrice(BigDecimal protectPrice) {
		this.protectPrice = protectPrice;
	}

	public List<ServiceType> getServiceTypes() {
		return serviceTypes;
	}

	public void setServiceTypes(List<ServiceType> serviceTypes) {
		this.serviceTypes = serviceTypes;
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
}
