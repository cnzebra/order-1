package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

public class Line {
	
	private Integer index;
	private String fromAddress;
	private String toAddress;
	private Date beginTime;
	private Date endTime;
	private User executorUser;
	private String title;
	
	public Line(){
		
	}
	
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public User getExecutorUser() {
		return executorUser;
	}
	public void setExecutorUser(User executorUser) {
		this.executorUser = executorUser;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public static class LineUtil{
		
		public static Line getLine(List<Line> list,Integer index){
			for(Line line :list){
				if(line.getIndex()==index){
					return line;
				}
			}
			return null;
		}
	}
}
