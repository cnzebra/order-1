package com.mrwind.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mrwind.common.bean.Result;
import com.mrwind.order.entity.ShopSender;
import com.mrwind.order.service.UserService;

@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/querySenderInfo", method = RequestMethod.GET)
	public Result querySenderInfo(@RequestParam("shopId") String shopId,
			@RequestParam("pageSize") Integer pageSize,
			@RequestParam("pageIndex") Integer pageIndex) {
		PageRequest page = new PageRequest(pageIndex - 1, pageSize);
		List<ShopSender> list=userService.queryShopSenderInfo(shopId, page);
		return Result.success(list);
	}
}
