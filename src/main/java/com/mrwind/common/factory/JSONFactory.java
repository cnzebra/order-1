package com.mrwind.common.factory;


import com.alibaba.fastjson.JSONObject;

public class JSONFactory {
	
	/**
	 * 成功信息默认为 “成功”,code=1
	 * @return 返回成功状态的JSONObject
	 */
	public static JSONObject getSuccessJSON(){
		JSONObject obj = new JSONObject();
		obj.put("code", "1");
		obj.put("message", "成功");
//		obj.put("content", "");
		return obj;
	}
	
	/**
	 * 自定义返回message信息,code=1
	 * @message 自定义返回message信息
	 * @return 返回成功状态的JSONObject
	 */
	public static JSONObject getSuccessJSON(String message){
		JSONObject obj = new JSONObject();
		obj.put("code", "1");
		obj.put("message", message);
//		obj.put("content", "");
		return obj;
	}
	
	/**
	 * 用于返回 0-业务请求被拒绝、没有数据等
	 * @message 错误信息
	 * @return
	 */
	public static JSONObject getfailJSON(String message){
		JSONObject obj = new JSONObject();
		obj.put("code", "0");
		obj.put("message", message);
		obj.put("content", "");
		return obj;
	}
	
	/**
	 * -1系统错误,请求参数错误
	 * @param message
	 * @return
	 */
	public static JSONObject getErrorJSON(String message){
		JSONObject obj = new JSONObject();
		obj.put("code", "-1");
		obj.put("message", message);
		obj.put("content", "");
		return obj;
	}


}
