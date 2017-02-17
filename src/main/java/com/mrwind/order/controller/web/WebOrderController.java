package com.mrwind.order.controller.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
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
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.ShopUser;
import com.mrwind.order.entity.User;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("web/order")
public class WebOrderController {

	Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public JSONObject create(@RequestBody JSONObject json) {
		Order order = JSONObject.toJavaObject(json, Order.class);
		List<Express> res = orderService.insert(order);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("content", res);
		return successJSON;
	}
	
	@ResponseBody
	@RequestMapping(value = "/create/list", method = RequestMethod.POST)
	public JSONObject createList(@RequestBody JSONObject json) {
		
		JSONObject shopJson = json.getJSONObject("shop");
		if(shopJson==null){
			return JSONFactory.getErrorJSON("商户信息不明，无法下单");
		}
		ShopUser shopUser = JSONObject.toJavaObject(shopJson, ShopUser.class);
		JSONObject senderJson = json.getJSONObject("sender");
		User sender=null;
		if(senderJson!=null){
			sender = JSONObject.toJavaObject(senderJson, User.class);
		}
		JSONArray jsonArray = json.getJSONArray("expressList");
		Iterator<Object> iterator = jsonArray.iterator();
		List<Order> list =new ArrayList<>();
		while(iterator.hasNext()){
			Order order = JSONObject.toJavaObject((JSONObject)iterator.next(),Order.class);
			order.setShop(shopUser);
			order.setSender(sender);
			list.add(order);
		}
		orderService.insert(list);
		
		return JSONFactory.getSuccessJSON();
	}
	
	

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.POST)
	public Result select(Order order) {
		Order res = orderService.selectByOrder(order);
		return Result.success(res);
	}
	
	@ResponseBody
	@RequestMapping(value = "/select/all", method = RequestMethod.POST)
	public Result selectAll(Order order) {
		Order res = orderService.selectByOrder(order);
		return Result.success(res);
	}  
}
