package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Express extends OrderBase {

	@Indexed
	private Long expressNo;
	@Indexed
	private Long bindExpressNo;
	private Date duiTime;
	private List<Line> lines;
	private Integer currentLine=1;
	private BigDecimal downMoney;

	public List<Line> getLines() {
		return lines;
	}
	
	public Express(){
		super();
	}
	
	public Express(OrderBase base) {
		this.status = base.status;
		this.shop = base.shop;
		this.sender = base.sender;
		this.receiver = base.receiver;
		this.category = base.category;
		this.remark = base.remark;
		this.orderUserType=base.orderUserType;
		this.createTime = base.createTime;
		this.updateTime = base.updateTime;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public Long getExpressNo() {
		return expressNo;
	}


	public void setExpressNo(Long expressNo) {
		this.expressNo = expressNo;
	}

	public Long getBindExpressNo() {
		return bindExpressNo;
	}

	public void setBindExpressNo(Long bindExpressNo) {
		this.bindExpressNo = bindExpressNo;
	}

	public Date getDuiTime() {
		return duiTime;
	}

	public void setDuiTime(Date duiTime) {
		this.duiTime = duiTime;
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

}
