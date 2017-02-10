package com.mrwind.common.util;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.constant.ConfigConstant;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HttpUtil {

	/***
	 * 查询内部用户的方法
	 * 
	 * @param userId
	 * @return
	 */
	public static JSONObject findPrivateUserInfo(String userId) {
		if (StringUtils.isEmpty(userId))
			return null;
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "WindCloud/account/common/info/";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL + userId);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONObject parseObject = JSONObject.parseObject(textEntity);
				if (parseObject.getString("code").equals("1")) {
					JSONObject jsonObject = parseObject.getJSONObject("user");
					JSONObject userJson = new JSONObject();
					userJson.put("id", jsonObject.get("id"));
					userJson.put("name", jsonObject.get("name"));
					userJson.put("tel", jsonObject.get("tel"));
					userJson.put("address", jsonObject.get("address"));
					userJson.put("avatar", jsonObject.get("avatar"));
					JSONObject gps = findUserGPS(userId).getJSONObject(0);
					if(gps!=null){
						JSONObject gpsDetail = gps.getJSONObject("gpsDetail");
						userJson.put("lng", gpsDetail.getDoubleValue("longitude"));
						userJson.put("lat", gpsDetail.getDoubleValue("latitude"));
					}
					userJson.put("job", jsonObject.get("job"));
					return userJson;
				}
			}
		}
		return null;
	}
	
	public static JSONObject getUserInfoByToken(String token) {
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST + "WindCloud/account/baseInfo/token");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "token " + token).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject json = JSONObject.parseObject(textEntity);
			return json;
		} else {
			return null;
		}
	}
	
	public static JSONArray findUserGPS(String userId) {
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "gps/api/query";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL + "?mansIds=" + userId);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONObject parseObject = JSONObject.parseObject(textEntity);
				if (parseObject.getString("code").equals("1")) {
					JSONArray resObj = parseObject.getJSONArray("content");
					return resObj;
				}
			}
		}
		return null;
	}

	/***
	 * 查询运单详情
	 * 
	 * @param orders
	 *            运单们
	 * @param params
	 *            查询的种子
	 * @return
	 */
	public static JSONArray findOrderInfo(String orders, String params) {
		StringBuffer requestUrl = new StringBuffer(ConfigConstant.API_PYTHON_HOST + "server/multi");
		Client client = Client.create();
		WebResource webResource = client.resource(requestUrl.toString());
		ClientResponse clientResponse = webResource.queryParam("only", params).queryParam("page", "1")
				.queryParam("count", orders.split(",").length + "").queryParam("numbers", orders)
				.queryParam("order_by", "-number").get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONArray jsonArray = JSONArray.parseArray(textEntity);
				return jsonArray;
			}
		}
		return new JSONArray();
	}

	public static JSONObject findUserInfo(String userId) {
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "WindData/customer/info";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL + "?id=" + userId);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONObject parseObject = JSONObject.parseObject(textEntity);
				if (parseObject.getString("code").equals("1")) {
					JSONObject resObj = parseObject.getJSONObject("content");
					resObj.put("job", "用户");
					return resObj;
				}
			}
		}
		return null;
	}

	public static Boolean sendWindDataLog(JSONObject json) {
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "WindData/log/create";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL);
		ClientResponse clientResponse = webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, json.toString());
		if (clientResponse.getStatus() == 200) {
			return true;
		}
		return false;
	}

	public static Boolean pushJsonDateToPhone(JSONObject json) {
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "WindChat/order/task/push";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL);
		ClientResponse clientResponse = webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, json.toString());
		if (clientResponse.getStatus() == 200) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("uid", "11e62c84fb1fa880bdaf22266a250d8f");
		json.put("data", "测试数据");

		pushJsonDateToPhone(json);
	}

	public static String getUserIdByToken(String token) {
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST + "WindCloud/account/baseInfo/token");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Authorization", "token " + token).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject json = JSONObject.parseObject(textEntity);
			return json.getString("id");
		} else {
			return "";
		}
	}
}
