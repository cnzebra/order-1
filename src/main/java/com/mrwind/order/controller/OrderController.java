package com.mrwind.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.util.JsonUtil;
import com.mrwind.order.amqp.OrderMqServer;
import com.mrwind.order.entity.Call;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.ShopSender;
import com.mrwind.order.service.OrderService;
import com.mrwind.order.service.UserService;

@Controller
@RequestMapping("admin")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderMqServer orderMqServer;
	
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Result create(@RequestBody JSONObject json) {
		Order order = JSONObject.toJavaObject(json, Order.class);
		orderService.insert(order);
		return Result.success();
	}

	@ResponseBody
	@RequestMapping(value = "/close", method = RequestMethod.GET)
	public Result close(@RequestBody JSONObject json) {
		return Result.success();
	}

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public Result select(@RequestBody JSONObject json) {
		Order order = JSONObject.toJavaObject(json, Order.class);
		Order order2 = orderService.select(order);
		return Result.success(order2);
	}

	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public Result update(@RequestBody JSONObject json) {
		return Result.success();
	}

	@ResponseBody
	@RequestMapping(value = "/call", method = RequestMethod.POST)
	public Result call(@RequestBody JSONObject json) {
		Call call = JSONObject.toJavaObject(json, Call.class);
		ShopSender shopSender = JSONObject.toJavaObject(json.getJSONObject("sender"), ShopSender.class);
		shopSender.setShopInfo(call.getShopInfo());
		userService.save(shopSender);
		Call res = orderService.saveCall(call);
		orderMqServer.sendDataToCrQueue(call);
		return Result.success(res);
	}

	@ResponseBody
	@RequestMapping(value = "/queryCall", method = RequestMethod.GET)
	public Result queryCall(@RequestParam("id") String id) {
		Call callInfo = orderService.queryCallInfo(id);
		return Result.success(callInfo);
	}

	@ResponseBody
	@RequestMapping(value = "/queryCallList", method = RequestMethod.GET)
	public Result queryCallList(@RequestParam("pageSize") Integer pageSize,
			@RequestParam("pageIndex") Integer pageIndex,
			@RequestParam(value = "only", defaultValue = "") String only) {
		PageRequest page = new PageRequest(pageIndex - 1, pageSize);
		Page<Call> callInfo = orderService.queryCallList(page);
		Object res = JsonUtil.filterProperty(callInfo.getContent(), only);
		return Result.success(res);
	}
}
