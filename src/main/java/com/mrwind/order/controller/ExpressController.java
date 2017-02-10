package com.mrwind.order.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.User;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("express")
public class ExpressController {

	@Autowired ExpressService expressService;
	
	@ResponseBody
	@RequestMapping(value = "/line", method = RequestMethod.POST)
	public Result line(Express express) {
		JSONObject res = expressService.selectByExpress(express);
		return Result.success(res);
	}
	
	@ResponseBody
	@RequestMapping(value = "/line/complete", method = RequestMethod.POST)
	public JSONObject completeLine(String expressNo,Integer lineIndex){
		
		expressService.completeLine(expressNo,lineIndex);
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/line/add", method = RequestMethod.POST)
	public JSONObject addLine(JSONArray jsonArray){
		
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/line/update", method = RequestMethod.POST)
	public JSONObject updateLine(JSONArray jsonArray){
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JSONObject create(@RequestBody JSONObject expressJson) {
		Express express = JSONObject.toJavaObject(expressJson, Express.class);
		
		List<Line> lines = new ArrayList<>();
		
		User executorUser= JSONObject.toJavaObject(expressJson.getJSONObject("executor"), User.class);
		Line line = new Line();
		line.setBeginTime(express.getCreateTime());
		line.setExecutorUser(executorUser);
		line.setFromAddress(express.getSender().getAddress());
		line.setIndex(1);
		lines.add(line);
		
		express.setLines(lines);
		expressService.initExpress(express);
		return JSONFactory.getSuccessJSON();
	}
}
