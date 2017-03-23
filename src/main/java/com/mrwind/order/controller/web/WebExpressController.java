package com.mrwind.order.controller.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mrwind.common.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	 *
	 * @param id 商户Id
	 * @param tel 发件人或收件人手机号码
	 * @param date 日期
	 * @param expressNo 订单号
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/selectByShopIdAndMode", method = RequestMethod.GET)
	public JSONObject selectByShopIdAndMode(String id,String status, String tel, String date, String expressNo,
											@RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
											@RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize) {
		if (StringUtils.isBlank(id)) {
			return JSONFactory.getfailJSON("商户Id不能为空");
		}
		Date parseDate = DateUtils.parseDate(date);
		Page<Express> expressPage = expressService.selectByShopIdAndMode(id,status, tel, expressNo, parseDate, pageIndex - 1, pageSize);
		if (expressPage != null) {
			JSONObject json = JSONFactory.getSuccessJSON();
			json.put("content", expressPage);
			return json;
		}
		return JSONFactory.getfailJSON("查询不到数据");
	}


	/**
	 * 微信订单多维度查询接口
	 *
	 * @param id
	 * @param status begin,create
	 * @param date
	 * @param dayType today,history
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/select/wechat/selectByShopIdAndMode", method = RequestMethod.GET)
	public JSONObject selectByShopIdForWeChat(String id, String status, String date, String dayType, String param,
											  @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
											  @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
		if (StringUtils.isBlank(id)) {
			return JSONFactory.getfailJSON("商户Id不能为空");
		}
		Date parseDate = DateUtils.parseDate(date);
		List<Express> expressList = expressService.selectByShopIdAndModeForWeChat(id, status, parseDate, dayType, param, pageIndex - 1, pageSize);
		if (expressList != null) {
			JSONObject json = JSONFactory.getSuccessJSON();
			json.put("content", expressList);
			return json;
		}
		return JSONFactory.getfailJSON("查询不到数据");
	}

}
