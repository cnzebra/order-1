package com.mrwind.order.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.Md5Util;
import com.mrwind.order.App;
import com.mrwind.order.service.OrderService;

@Controller
@RequestMapping("")
public class OrderController {

	Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;

	@ResponseBody
	@RequestMapping(value = "/pay/{userId}", method = RequestMethod.POST)
	public JSONObject pay(@RequestBody List<String> listExpress,@PathVariable("userId")String userId) {
		JSONObject res = orderService.pay(listExpress,userId);
		return res;
	}
	
	@ResponseBody
	@RequestMapping(value = "/query/price", method = RequestMethod.POST)
	public JSONObject queryPrice(@RequestBody List<String> listExpress) {
		JSONObject res = orderService.queryPrice(listExpress);
		return res;
	}
	
	@ResponseBody
	@RequestMapping(value = "/request/tran", method = RequestMethod.GET)
	public JSONObject requestTran(String tranNo) {
		return orderService.queryTranSactionDetail(tranNo);
	}
	
	@ResponseBody
	@RequestMapping(value = "/tran/lock", method = RequestMethod.GET)
	public JSONObject requestTranLock(String tranNo) {
		return orderService.lockOrder(tranNo);
	}
	
	@ResponseBody
	@RequestMapping(value = "/pay/callback", method = RequestMethod.POST)
	public JSONObject payCallback(@RequestBody JSONObject json) {
		String tranNo = json.getString("tranNo");
		String requestToken = json.getString("requestToken");
		String token = Md5Util.string2MD5(tranNo+App.SESSION_KEY);
		if(!requestToken.equals(token)){
			return JSONFactory.getErrorJSON("请求非法");
		}
		String res = orderService.payCallback(tranNo);
		if(StringUtils.isBlank(res)){
			return JSONFactory.getSuccessJSON();
		}
		return JSONFactory.getErrorJSON(res);
	}
	
	@ResponseBody
	@RequestMapping(value = "/request/tran/money", method = RequestMethod.GET)
	public JSONObject requestTranMoney(String tranNo) {
		JSONObject tranSactionDetail = orderService.queryTranSactionDetail(tranNo);
		if(tranSactionDetail.getString("code").equals("1")){
			return JSONFactory.getSuccessJSON(tranSactionDetail.getString("totalPrice"));
		}
		return JSONFactory.getErrorJSON("交易已过期");
	}
}
