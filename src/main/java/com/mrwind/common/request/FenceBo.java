package com.mrwind.common.request;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.List;

/**
 * 围栏信息类
 */
public class FenceBo {

	private String id;
	private String name;
	private List<Man> mans;		//围栏下的快递员们
	@JSONField(name = "update_time")
	private Date updateTime;
	@JSONField(name = "create_time")
	private Date createTime;

	private boolean supportSMainLine;//是否为小干线
	private String illustration;//描述

	private double distance;//距离
	private String nodeId;//站点id
	
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
	public List<Man> getMans() {
		return mans;
	}
	public void setMans(List<Man> mans) {
		this.mans = mans;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FenceBo other = (FenceBo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isSupportSMainLine() {
		return supportSMainLine;
	}

	public void setSupportSMainLine(boolean supportSMainLine) {
		this.supportSMainLine = supportSMainLine;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getIllustration() {
		return illustration;
	}

	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
}
