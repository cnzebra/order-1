package com.mrwind.order.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.bean.Result;
import com.mrwind.common.util.JsonUtil;
import com.mrwind.order.entity.Call;
import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.Shop;
import com.mrwind.order.entity.ShopReceiver;
import com.mrwind.order.entity.ShopSender;
import com.mrwind.order.service.OrderService;
import com.mrwind.order.service.UserService;

@Controller
@RequestMapping("admin")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
//	@Autowired
//	private OrderMqServer orderMqServer;
	
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Result create(@RequestBody JSONObject json) {
		Order order = JSONObject.toJavaObject(json, Order.class);
		Shop shopInfo =JSONObject.toJavaObject( json.getJSONObject("shop"),Shop.class);
		ShopSender shopSender = JSONObject.toJavaObject(json.getJSONObject("sender"), ShopSender.class);
		shopSender.setShopInfo(shopInfo);
		userService.saveSender(shopSender);
		
		ShopReceiver shopReceiver = JSONObject.toJavaObject(json.getJSONObject("receiver"), ShopReceiver.class);
		shopReceiver.setShopInfo(shopInfo);
		userService.saveReceiver(shopReceiver);
		Order res = orderService.insert(order);
		return Result.success(res);
	}
	
	@ResponseBody
	@RequestMapping(value = "/createList", method = RequestMethod.POST)
	public Result createList(@RequestBody JSONArray jsonArray) {
		List<Order> list=new ArrayList<>();
		for (int i=0;i<jsonArray.size();i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Order order = JSONObject.toJavaObject(json, Order.class);
			Order res = orderService.insert(order);
			list.add(res);
		}
		return Result.success(list);
	}

	@ResponseBody
	@RequestMapping(value = "/cancel", method = RequestMethod.PATCH)
	public Result cancel(@RequestParam("orderNumber")Long orderNumber,@RequestParam("subStatus")String subStatus) {
		orderService.cancelOrder(orderNumber,subStatus);
		return Result.success();
	}
	
	@ResponseBody
	@RequestMapping(value="/pricing",method=RequestMethod.POST)
	public Result pricing(@RequestBody JSONObject json){
		Long orderNumber = json.getLong("orderNumber");
		Fence fence = JSONObject.toJavaObject(json, Fence.class);
		orderService.submitOrderPriced(orderNumber, fence);
		return Result.success();
	}
	
	@ResponseBody
	@RequestMapping(value="/complete",method=RequestMethod.PATCH)
	public Result complete(@RequestParam("orderNumber")Long orderNumber){
		orderService.completeOrder(orderNumber);
		return Result.success();
	}
	
	@ResponseBody
	@RequestMapping(value="/errorComplete",method=RequestMethod.PATCH)
	public Result errorComplete(@RequestParam("orderNumber")Long orderNumber,@RequestParam("subStatus")String subStatus){
		orderService.errorCompleteOrder(orderNumber,subStatus);
		return Result.success();
	}

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public Result select(@RequestParam("orderNumber")Long orderNumber,@RequestParam(value="only",defaultValue="")String only) {
		Order order = orderService.selectById(orderNumber);
		Object res = JsonUtil.filterProperty(order, only);
		return Result.success(res);
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
		userService.saveSender(shopSender);
		Call res = orderService.saveCall(call);
//		orderMqServer.sendDataToCrQueue(call);
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
