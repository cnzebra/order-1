package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.annotation.JSONField;

@Document
public class Comment {
	
	@Id
	private String id;
	private ShopUser shop;
	private String expressNo;
	private Integer Star;
	private Set<String> title;
	private String content;
	private Date createTime;
	private Date updateTime;
	@JSONField(serialize=false)
	private List<Line> lines;
	public ShopUser getShop() {
		return shop;
	}
	public void setShop(ShopUser shop) {
		this.shop = shop;
	}
	public Integer getStar() {
		return Star;
	}
	public void setStar(Integer star) {
		Star = star;
	}
	public Set<String> getTitle() {
		return title;
	}
	public void setTitle(Set<String> title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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
	public List<Line> getLines() {
		return lines;
	}
	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
}
