package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.annotation.JSONField;

@Document
public class Express extends OrderBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7265949299425489281L;
	@Indexed
	private String expressNo;
	@Indexed
	private Date dueTime;
	private String mode;
	private List<Line> lines;
	private Integer currentLine=1;
	private String type;
	private BigDecimal downMoney;
	private Date planEndTime;
	private Date realEndTime;
	@JSONField(serialize=false)
	private Address endAddress;

	public List<Line> getLines() {
		return lines;
	}
	
	public Express(){
		super();
	}

	public Express(Order order) {
		this.status = order.status;
		this.shop = order.shop;
		this.sender = order.sender;
		this.receiver = order.receiver;
		this.category = order.category;
		this.remark = order.remark;
		this.bindExpressNo=order.bindExpressNo;
		this.createTime = order.createTime;
		this.updateTime = order.updateTime;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public Integer getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(Integer currentLine) {
		this.currentLine = currentLine;
	}

	public BigDecimal getDownMoney() {
		return downMoney;
	}

	public void setDownMoney(BigDecimal downMoney) {
		this.downMoney = downMoney;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public Date getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(Date realEndTime) {
		this.realEndTime = realEndTime;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Address getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(Address endAddress) {
		this.endAddress = endAddress;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
