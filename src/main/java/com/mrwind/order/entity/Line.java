package com.mrwind.order.entity;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.util.HttpUtil;

public class Line {

	private Integer index;
	private String status;
	private Fence fence;
	private String node;
	private Date planTime;
	private Date realTime;
	private User executorUser;
	private String title;

	public Line() {

	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public User getExecutorUser() {
		return executorUser;
	}

	public void setExecutorUser(User executorUser) {
		this.executorUser = executorUser;
	}



	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static class LineUtil {

		public static Line getLine(List<Line> list, Integer index) {
			for (Line line : list) {
				if (line.getIndex() == index) {
					return line;
				}
			}
			return null;
		}

		public static Line caseToLine(JSONObject json) {
			Line line = new Line();

			line.setPlanTime(json.getDate("predict_time"));
			User executorUser = JSONObject.toJavaObject(json.getJSONObject("operator"), User.class);
			Address userGPS = HttpUtil.findUserGPS(executorUser.getId());
			if(userGPS!=null){
				executorUser.setLat(userGPS.getLat());
				executorUser.setLng(userGPS.getLng());
			}

			line.setExecutorUser(executorUser);
			line.setIndex(json.getInteger("node_num"));
			if (json.getJSONObject("fence") != null) {
				Fence fence = JSONObject.toJavaObject(json.getJSONObject("fence"), Fence.class);
				line.setFence(fence);
			}
			if (json.getJSONObject("node") != null) {
				String node = json.getJSONObject("node").getString("name");
				line.setNode(node);
			}
			return line;
		}
	}

	public Fence getFence() {
		return fence;
	}

	public void setFence(Fence fence) {
		this.fence = fence;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Date getPlanTime() {
		return planTime;
	}

	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}

	public Date getRealTime() {
		return realTime;
	}

	public void setRealTime(Date realTime) {
		this.realTime = realTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
