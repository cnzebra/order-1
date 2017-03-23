package com.mrwind.order.controller;

import javax.servlet.http.HttpServletResponse;

import com.mrwind.common.cache.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.entity.Address;
import com.mrwind.order.entity.Category;
import com.mrwind.order.entity.Express;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("express")
public class ExpressController {

	@Autowired
	ExpressService expressService;
	@Autowired
	RedisCache redisCache;

	@ResponseBody
	@RequestMapping(value = "/selectDev",method = RequestMethod.GET)
	public JSONObject select(String expressNo) {
		Express res = expressService.selectByNo(expressNo);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", res);
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public JSONObject select(String encode,String count) {
		String expressNo = redisCache.getString(encode);
		Express res = expressService.selectByNo(expressNo);
		redisCache.set(expressNo,60*60*24*15,count);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", res);
		successJSON.put("count",count);
		return successJSON;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update/pricing", method = RequestMethod.POST)
	public JSONObject updatePrice(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			JSONFactory.getErrorJSON("没有登录信息");
		}
		token = token.substring(6);
		String adminUserId = HttpUtil.getUserIdByToken(token);
		if (StringUtils.isEmpty(adminUserId)) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}
		String expressNo = json.remove("expressNo").toString();
		
		if (StringUtils.isBlank(expressNo)) {
			return JSONFactory.getErrorJSON("运单号不能为空");
		}

		JSONObject calculatePrice = HttpUtil.calculatePrice(json);
		Category category = JSON.toJavaObject(calculatePrice, Category.class);
		expressService.updateCategory(expressNo,category);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public JSONObject update(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			JSONFactory.getErrorJSON("没有登录信息");
		}
		token = token.substring(6);
		String adminUserId = HttpUtil.getUserIdByToken(token);
		if (StringUtils.isEmpty(adminUserId)) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}

		Express express = JSONObject.toJavaObject(json, Express.class);
		if (express.getExpressNo() == null) {
			return JSONFactory.getErrorJSON("运单号不能为空");
		}
		expressService.updateExpress(express);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/cancel/{expressNo}", method = RequestMethod.DELETE)
	public JSONObject cancel(@PathVariable("expressNo") String expressNo, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			JSONFactory.getErrorJSON("没有登录信息");
		}
		token = token.substring(6);
		String adminUserId = HttpUtil.getUserIdByToken(token);
		if (StringUtils.isEmpty(adminUserId)) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}

		if (StringUtils.isEmpty(expressNo)) {
			return JSONFactory.getErrorJSON("运单号不能为空");
		}
		
		expressService.cancelExpress(expressNo);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public JSONObject complete(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			return JSONFactory.getErrorJSON("没有登录信息");
		}

		String expressNo = json.getString("expressNo");
		Address endAddress = JSON.toJavaObject(json, Address.class);
		
		if (StringUtils.isEmpty(expressNo)) {
			return JSONFactory.getErrorJSON("订单号不能为空");
		}
		token = token.substring(6);
		JSONObject userInfo = HttpUtil.getUserInfoByToken(token);
		if (userInfo == null) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}

		expressService.completeExpress(expressNo,endAddress,userInfo);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/completeByCode", method = RequestMethod.POST)
	public JSONObject completeByCode(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
									 HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			return JSONFactory.getErrorJSON("没有登录信息");
		}

		String expressNo = json.getString("expressNo");

		String verifyCode = json.getString("verifyCode");

		if (StringUtils.isEmpty(verifyCode)) {
			return JSONFactory.getErrorJSON("验证码缺失");
		}

		if (StringUtils.isEmpty(expressNo)) {
			return JSONFactory.getErrorJSON("订单号不能为空");
		}
		token = token.substring(6);
		JSONObject userInfo = HttpUtil.getUserInfoByToken(token);
		if (userInfo == null) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}
		return expressService.completeByCode(expressNo, verifyCode, userInfo);
	}


	@ResponseBody
	@RequestMapping(value = "/error/complete", method = RequestMethod.POST)
	public JSONObject errorComplete(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			return JSONFactory.getErrorJSON("没有登录信息");
		}

		String expressNo = json.getString("expressNo");
		Address endAddress = JSON.toJavaObject(json, Address.class);
		if (StringUtils.isEmpty(expressNo)) {
			return JSONFactory.getErrorJSON("订单号不能为空");
		}
		
		token = token.substring(6);
		JSONObject userInfo = HttpUtil.getUserInfoByToken(token);
		if (userInfo == null) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}

		return expressService.errorComplete(expressNo,endAddress, userInfo);

	}

	/**
	 * 根据用户id查询与其有关系的运单
	 * @param userId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/relationship", method = RequestMethod.GET)
	public JSONObject errorComplete(@RequestParam String userId, HttpServletResponse response) {
		if (StringUtils.isEmpty(userId))
			return JSONFactory.getErrorJSON("用户id不能为空");
		return expressService.findRelationship(userId);
	}
}
