package com.mrwind.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.order.entity.Order;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("admin")
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Result create(@RequestBody JSONObject json) {
		 Order order = JSONObject.toJavaObject(json, Order.class);
		 orderService.insert(order);
		return Result.success();
	}
	
	@ResponseBody
	@RequestMapping(value = "/close", method = RequestMethod.GET)
	public Result close(@RequestBody JSONObject json){
		return Result.success();
	}
	
	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public Result select(@RequestBody JSONObject json){
		Order order = JSONObject.toJavaObject(json, Order.class);
		Order order2 = orderService.select(order);
		return Result.success(order2);
	}
	
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public Result update(@RequestBody JSONObject json){
		return Result.success();
	}

}
