package com.mrwind.order.controller.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.JsonUtil;
import com.mrwind.order.entity.Express;
import com.mrwind.order.service.ExpressService;

@Controller
@RequestMapping("web/express")
public class WebExpressController {

	@Autowired
	ExpressService expressService;

	@ResponseBody
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	public JSONObject select(String expressNo) {
		Express res = expressService.selectByExpressNo(expressNo);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", res);
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/select/shop", method = RequestMethod.GET)
	public JSONObject select(String shopId, Integer pageIndex, Integer pageSize) {
		Page<Express> selectByShop = expressService.selectByShop(shopId, pageIndex - 1, pageSize);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", selectByShop);
		return successJSON;
	}

	@ResponseBody
	@RequestMapping(value = "/select/all", method = RequestMethod.POST)
	public JSONObject selectAll(@RequestBody List<Object> express) {
		List<String> expressList = new ArrayList<>();
		Iterator<Object> iterator = express.iterator();
		while (iterator.hasNext()) {
			expressList.add(iterator.next().toString());
		}
		List<Express> res = expressService.selectByExpressNo(expressList);
		Object filterProperty = JsonUtil.filterProperty(res, "");
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", filterProperty);
		return successJSON;
	}

	/**
	 * 批量标记打印
	 *
	 * @param list 要标记的订单号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/printed", method = RequestMethod.POST)
	public JSONObject printExpress(@RequestBody List<Object> list) {
		if(list == null){
			return JSONFactory.getfailJSON("操作失败");
		}
		List<String> expressList = new ArrayList<>();
		Iterator<Object> iterator = list.iterator();
		while (iterator.hasNext()) {
			expressList.add(iterator.next().toString());
		}
		return expressService.updateExpressPrinted(expressList);
	}

	/**
	 * 根据发件人Id将订单以dueTime分钟为单位进行分组
	 * @param id 发件人Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectByShopIdAndMode", method = RequestMethod.GET)
	public JSONObject selectByShopIdAndMode(String id, String tel, Date date, String expressNo, Integer pageIndex, Integer pageSize) {
		if(StringUtils.isBlank(id)){
			return JSONFactory.getfailJSON("寄件人Id不能为空");
		}
		if(pageIndex == null){
			pageIndex = 1;
		}
		if(pageSize == null){
			pageSize = 100;
		}
		List<Express> expressList =  expressService.selectByShopIdAndMode(id,tel,expressNo,date,pageIndex -1,pageSize);
		if(expressList != null){
			JSONObject json = JSONFactory.getSuccessJSON();
			json.put("content",expressList);
			return json;
		}
		return JSONFactory.getfailJSON("查询不到数据");
	}


}
