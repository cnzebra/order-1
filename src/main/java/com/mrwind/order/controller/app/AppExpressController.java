package com.mrwind.order.controller.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.entity.Category;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.User;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("app/express")
public class AppExpressController {

	@Autowired ExpressService expressService;
	
	/***
	 * 配送员加单
	 * @param expressJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JSONObject create(@RequestBody JSONObject expressJson) {
		Express express = JSONObject.toJavaObject(expressJson, Express.class);
		express.setCreateTime(Calendar.getInstance().getTime());

		List<Line> lines = new ArrayList<>();
		User executorUser = JSONObject.toJavaObject(expressJson.getJSONObject("executor"), User.class);
		if(executorUser==null){
			return JSONFactory.getErrorJSON("找不到配送员信息，无法加单！");
		}
		Line line = new Line();
		line.setPlanTime(express.getCreateTime());
		line.setExecutorUser(executorUser);
		line.setNode(express.getSender().getAddress());
		line.setIndex(1);
		lines.add(line);

		express.setLines(lines);
		JSONObject calculatePrice = HttpUtil.calculatePrice(expressJson.getJSONObject("category"));
		Category javaObject = JSON.toJavaObject(calculatePrice, Category.class);
		express.setCategory(javaObject);
		Express initExpress = expressService.initExpress(express);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", initExpress);
		return successJSON;
	}
	
	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public Result select(@RequestBody Express express) {
		Express res = expressService.selectByExpress(express);
		return Result.success(res);
	}
	
	@ResponseBody
	@RequestMapping(value = "/select/all/{pageIndex}_{pageSize}", method = RequestMethod.POST)
	public Result selectAll(@RequestBody Express express,@PathVariable("pageIndex")Integer pageIndex,@PathVariable("pageSize")Integer pageSize) {
		Page<Express> selectAllByExpress = expressService.selectAllByExpress(express,pageIndex-1,pageSize);
		return Result.success(selectAllByExpress);
	} 
	
	@ResponseBody
	@RequestMapping(value = "/select/param/{pageIndex}_{pageSize}", method = RequestMethod.GET)
	public Result selectParamAll(String param,String fenceName,@PathVariable("pageIndex")Integer pageIndex,@PathVariable("pageSize")Integer pageSize) {
		List<Express> selectAll = expressService.selectAll(param, fenceName);
		return Result.success(selectAll);
	}  
}
