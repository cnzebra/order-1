package com.mrwind.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("express")
public class ExpressController {

	@Autowired
	ExpressService expressService;

	@ResponseBody
	@RequestMapping(value = "/line", method = RequestMethod.GET)
	public JSONObject line(String expressNo) {
		Express resExpress = expressService.selectByExpressNo(expressNo);
		if(resExpress==null){
			return JSONFactory.getErrorJSON("没有数据");
		}
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", resExpress.getLines());
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/line/complete", method = RequestMethod.POST)
	public JSONObject completeLine(@RequestBody JSONObject param) {
		String expressNo = param.getString("expressNo");
		Integer lineIndex = param.getInteger("lineIndex");
		expressService.completeLine(expressNo, lineIndex);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/line/add/{expressNo}", method = RequestMethod.PUT)
	public JSONObject addLine(@RequestBody String jsonString,@PathVariable("expressNo")Long expressNo) {
		if(StringUtils.isBlank(jsonString)){
			return JSONFactory.getErrorJSON("参数数据不正确");
		}
		List<Line> list = JSONArray.parseArray(jsonString, Line.class);
		expressService.addLine(expressNo,list);
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/line/remove", method = RequestMethod.DELETE)
	public JSONObject deleteLine(Long expressNo,Integer lineIndex) {
		if(expressNo==null){
			return JSONFactory.getErrorJSON("参数数据不正确");
		}
		expressService.removeLine(expressNo, lineIndex);
		return JSONFactory.getSuccessJSON();
	}

	@ResponseBody
	@RequestMapping(value = "/line/update/{expressNo}", method = RequestMethod.POST)
	public JSONObject updateLine(@RequestBody List<Line> list,@PathVariable("expressNo")Long expressNo) {
		expressService.updateLine(expressNo,list);
		return JSONFactory.getSuccessJSON();
	}

//	@ResponseBody
//	@RequestMapping(value = "/create", method = RequestMethod.POST)
//	public JSONObject create(@RequestBody JSONObject expressJson) {
//		Express express = JSONObject.toJavaObject(expressJson, Express.class);
//
//		List<Line> lines = new ArrayList<>();
//
//		User executorUser = JSONObject.toJavaObject(expressJson.getJSONObject("executor"), User.class);
//		Line line = new Line();
//		line.setBeginTime(express.getCreateTime());
//		line.setExecutorUser(executorUser);
//		line.setFromAddress(express.getSender().getAddress());
//		line.setIndex(1);
//		lines.add(line);
//
//		express.setLines(lines);
//		expressService.initExpress(express);
//		return JSONFactory.getSuccessJSON();
//	}

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

		Express express = JSONObject.toJavaObject(json, Express.class);
		if (express.getExpressNo() == null) {
			return JSONFactory.getErrorJSON("运单号不能为空");
		}

		expressService.updateCategory(express);
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
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public JSONObject cancel(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
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
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public JSONObject complete(@RequestBody JSONObject json, @RequestHeader("Authorization") String token,
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
	@RequestMapping(value = "/error/complete", method = RequestMethod.POST)
	public JSONObject errorComplete(@RequestBody List<Long> list, @RequestHeader("Authorization") String token,
			HttpServletResponse response) {

		if (StringUtils.isEmpty(token)) {
			 return JSONFactory.getErrorJSON("没有登录信息");
		}

		if (list == null || list.size() == 0) {
			return JSONFactory.getErrorJSON("参数订单号不能为空");
		}
		token = token.substring(6);
		JSONObject userInfo = HttpUtil.getUserInfoByToken(token);
		if (userInfo == null) {
			response.setStatus(401);
			return JSONFactory.getErrorJSON("请登录!");
		}

		return expressService.errorComplete(list,userInfo);

	}
}
