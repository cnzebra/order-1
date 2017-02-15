package com.mrwind.order.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("order")
public class OrderController {

	Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

	@ResponseBody
	@RequestMapping(value = "/pay", method = RequestMethod.POST)
	public JSONObject pay(@RequestBody List<Long> listExpress) {
		JSONObject res = orderService.pay(listExpress);
		return res;
	}
	
	@ResponseBody
	@RequestMapping(value = "/request/tran", method = RequestMethod.GET)
	public JSONObject pay(String tranNo) {
		return orderService.queryTranSactionDetail(tranNo);
	}
}
