package com.mrwind.common.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.mrwind.common.requestModel.threeone.Location;

/**
 * 节点的信息
 */
public class Node {

	private String id;
	private String name;			//节点的名称
	@JSONField(name = "time_table")
	private TimeTable[] timeTable;	//节点的时间计划
	private Location loc;			//节点的位置

	public Node() {
	}

	public Node(String id, String name) {
		this.id = id;
		this.name = name;
	}

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
	public TimeTable[] getTimeTable() {
		return timeTable;
	}
	public void setTimeTable(TimeTable[] timeTable) {
		this.timeTable = timeTable;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
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
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}