package com.mrwind.order.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/***
 * 订单收据
 * @author zhoujie
 *
 */
@Document
public class OrderReceipt {

	@Id
	private String id;
	@Indexed
	private String tranNo;
	@Indexed
	private String expressNo;
	private BigDecimal price;
	private Date createTime;
	private Category category;
	private User sender;
	private User receiver;
	protected String bindExpressNo;

	public OrderReceipt(){
		
	}
	
	public OrderReceipt(Express express){
		this.expressNo=express.getExpressNo();
		this.price=express.getCategory().getTotalPrice();
		this.createTime=Calendar.getInstance().getTime();
		this.category=express.getCategory();
		this.sender = express.getSender();
		this.bindExpressNo = express.getBindExpressNo();
		this.receiver = express.getReceiver();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}

	public String getTranNo() {
		return tranNo;
	}

	public void setTranNo(String tranNo) {
		this.tranNo = tranNo;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public String getBindExpressNo() {
		return bindExpressNo;
	}

	public void setBindExpressNo(String bindExpressNo) {
		this.bindExpressNo = bindExpressNo;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}
}
