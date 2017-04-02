package com.mrwind.order.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.NumberUtils;
import com.mrwind.order.service.ExpressBindService;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("express/bind")
public class ExpressBindController {
	
	@Autowired ExpressService expressService;
	
	@Autowired ExpressBindService expressBindService;
	
	@ResponseBody
	@RequestMapping(value = "/write", method = RequestMethod.PUT)
	public JSONObject write(String expressNo,String bindExpressNo) {
		
		if(!NumberUtils.isNumeric1(bindExpressNo)){
			return JSONFactory.getErrorJSON("不允许使用非数字的运单号进行绑定");
		}
		String res = expressBindService.bindExpress(expressNo,bindExpressNo);
		if(StringUtils.isNotBlank(res)){
			return JSONFactory.getErrorJSON(res);
		}
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public JSONObject delete(String expressNo) {
		String res = expressBindService.cancelExpressBindNo(expressNo);
		if(StringUtils.isNotBlank(res)){
			return JSONFactory.getErrorJSON(res);
		}
		return JSONFactory.getSuccessJSON();
	}
	
	@ResponseBody
	@RequestMapping(value = "/tel", method = RequestMethod.POST)
	public JSONObject bindByTel(JSONObject json) {
		String shopId = json.getString("shopId");
		String bindExpressNo = json.getString("bindExpressNo");
		String tel = json.getString("tel");
		Boolean res = expressBindService.bindExpressByTel(tel, shopId, bindExpressNo);
		if(!res){
			return JSONFactory.getErrorJSON("没有可绑定的运单");
		}
		return JSONFactory.getSuccessJSON();
	}

}
