package com.mrwind.common.util;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mrwind.common.constant.ConfigConstant;
import com.mrwind.order.App;
import com.mrwind.order.entity.Address;
import com.mrwind.order.entity.Category;
import com.mrwind.order.entity.Express;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.log4j.Logger;

public class HttpUtil {

	private static Logger log = Logger.getLogger(HttpUtil.class);

	/**
	 * 余额支付接口
	 * 
	 * @param tranNo
	 * @return
	 */
	public static boolean balancePay(String tranNo) {
		Client client = Client.create();
		WebResource webResource = client.resource(
				ConfigConstant.API_JAVA_HOST + "merchant/account/accountBalance/balanceDiscount?tranNo=" + tranNo);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONObject parseObject = JSONObject.parseObject(textEntity);
				if (parseObject.getString("code").equals("1")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 发送给指定userid短信
	 * 
	 * @param content
	 * @param userIds
	 */
	public static void sendSMSToUserId(String content, Collection<String> userIds) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("userIds", userIds);
		jsonObject.put("mrContent", content);
		// 暂时随意填，防止被过滤
		jsonObject.put("eventId", 1);
		jsonObject.put("modelId", 12);
		post(ConfigConstant.API_JAVA_HOST + App.MSG_SEND_USERID, jsonObject.toJSONString());
	}

	/**
	 * 发送给指定手机号码短信
	 *
	 * @param content
	 * @param tels
	 */
	public static void sendSMSToUserTel(String content,String tels) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("tel", tels);
		jsonObject.put("mrContent", content);
		// 暂时随意填，防止被过滤
		jsonObject.put("eventId", 1);
		jsonObject.put("modelId", 12);
		post(ConfigConstant.API_JAVA_HOST + App.MSG_SEND_TEL, jsonObject.toJSONString());
	}

	private static JSONObject post(String url, String parameter) {
		Client client = new Client();
		WebResource webResource = client.resource(url);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class,
				parameter);
		if (clientResponse.getStatus() == 200) {
			String result = clientResponse.getEntity(String.class);
			return JSON.parseObject(result);
		}
		JSONObject result = new JSONObject();
		result.put("errCode", clientResponse.getStatus());
		result.put("errMsg", clientResponse.getEntity(String.class));
		return result;
	}

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
					Address gps = findUserGPS(userId);
					if (gps != null) {
						userJson.put("lng",gps.getLng());
						userJson.put("lat", gps.getLat());
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

	public static Address findUserGPS(String userId) {
		String ACCOUNT_TOKEN_URL = ConfigConstant.API_JAVA_HOST + "gps/api/getGps";
		Client client = Client.create();
		WebResource webResource = client.resource(ACCOUNT_TOKEN_URL + "?userId=" + userId);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			if (textEntity != null) {
				JSONObject parseObject = JSONObject.parseObject(textEntity);
				if (parseObject.getString("code").equals("1")) {
					return JSONObject.toJavaObject(parseObject.getJSONObject("content"),Address.class);
				}
			}
		}
		return null;
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
		ClientResponse clientResponse = webResource.type(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE).post(
				ClientResponse.class, JSONObject.toJSONString(json, SerializerFeature.DisableCircularReferenceDetect));
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
		Express express = new Express();
		Category category=new Category();
		category.setDistance(100.1);
		express.setCategory(category);
		
		update(express);
		System.out.println(express.getCategory().getDistance());
	}

	private static void update(Express express) {
		// TODO Auto-generated method stub
		express.getCategory().setDistance(200.0);
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

	public static Boolean selectUserProtocol(String uid, String protocolNumber) {
		// TODO Auto-generated method stub
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST
				+ "merchant/protocol/findByShopIdAndProtocolNum?shopId=" + uid + "&protocolNum=" + protocolNumber);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject json = JSONObject.parseObject(textEntity);
			if (json.getString("code").equals("1")) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 定价接口
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject calculatePrice(JSONObject json) {
		// TODO Auto-generated method stub
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST + "Category/calculatePrice");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class,
				json.toString());
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return res.getJSONObject("data");
			}
		}
		return null;
	}
	
	/***
	 * 定价接口
	 * 
	 * @param json
	 * @return
	 */
	public static JSONObject calculatePrice(String shopId,Integer weight,Integer distance) {
		// TODO Auto-generated method stub
		JSONObject json=new JSONObject();
		json.put("shopId", shopId);
		json.put("weight", weight);
		json.put("distance", distance);
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST + "Category/calculatePrice");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class,
				json.toString());
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return res.getJSONObject("data");
			}
		}
		return null;
	}

	public static JSONObject findPersion(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		Client client = Client.create();
		WebResource webResource = client.resource(ConfigConstant.API_JAVA_HOST + "WindData/util/findPerson");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class,
				jsonObject.toString());
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return res.getJSONObject("content");
			}
		}
		return null;
	}

	public static JSONArray findExpressMission(String expressNo) {
		Client client = Client.create();
		WebResource webResource = client.resource(
				ConfigConstant.API_JAVA_HOST + "WindMissionAdapter/missonInfo/selectTop2Mission?order=" + expressNo);
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return res.getJSONArray("content");
			}
		}
		return null;
	}
	
	/**
	 * 获取距离
	 * @param toLat
	 * @param toLng
	 * @param fromLat
	 * @param fromLng
	 * @return
	 */
	public static Integer getDistance(Double toLat,Double toLng,Double fromLat,Double fromLng) {
		Client client = Client.create();
		JSONObject json=new JSONObject();
		json.put("toUserLat", toLat);
		json.put("toUserLng", toLng);
		json.put("fromUserLat", fromLat);
		json.put("fromUserLng", fromLng);
		WebResource webResource = client.resource(
				ConfigConstant.API_JAVA_HOST + "WindData/util/driver/distance");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class,json.toJSONString());
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return res.getInteger("content");
			}
		}
		return 0;
	}

	public static Boolean compileExpressMission(JSONArray json) {
		Client client = Client.create();
		WebResource webResource = client
				.resource(ConfigConstant.API_JAVA_HOST + "WindMissionAdapter/mission/compileMissionsByOrder");
		ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class,
				json.toString());
		if (clientResponse.getStatus() == 200) {
			String textEntity = clientResponse.getEntity(String.class);
			JSONObject res = JSONObject.parseObject(textEntity);
			if (res.getString("code").equals("1")) {
				return true;
			}
		}
		return false;
	}

	//创建收件行程及订单
	public static void createReceiveMission(final List<Express> express){
		JSONArray jsonArray = (JSONArray) JSONArray.toJSON(express);
		JSONObject result = post(ConfigConstant.API_JAVA_HOST + "WindMissionAdapter/mission/createReceiveMission", jsonArray.toJSONString());
		if (result.containsKey("errorCode")){
			log.info("类HttpUtil 方法createReceiveMission");
			log.info("errorCode" + result.getInteger("errCode"));
			log.info("errorMessage" + result.getString("errMsg"));
		}
	}
}
