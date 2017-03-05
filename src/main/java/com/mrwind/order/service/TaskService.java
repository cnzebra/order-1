package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gezbox.mrwind.service.netty.NettyConnectClient;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.common.util.UUIDUtils;
import com.mrwind.order.repositories.ExpressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mrwind.order.App;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class TaskService {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ExpressDao expressDao;

	@Autowired
	ExpressService expressService;

	@Autowired
	RedisCache redisCache;

	public void sendOrder() {
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.HOUR_OF_DAY, -2);

		List<Express> list = expressDao.findUnBegin(instance.getTime());
		if (list == null || list.size() == 0)
			return;

		Map<String, List<Express>> map = new HashMap<>();
		for (Express express : list) {
			expressDao.updateStatus(express.getExpressNo(), App.ORDER_BEGIN, App.ORDER_PRE_CREATED);

			List<Express> listTmp = map.get(express.getExpressNo());
			if (listTmp == null) {
				listTmp = new ArrayList<>();
			}
			listTmp.add(express);
			map.put(express.getExpressNo(), listTmp);
		}

		for (Map.Entry<String, List<Express>> entry : map.entrySet()) {
			expressService.sendExpressLog21010(entry.getValue());
		}
	}


	public void sendWarning(){
		List<Express> specialExpresses = expressDao.findByTypeAndStatusAndSubStatus(App.ORDER_SPECIAL, App.ORDER_BEGIN, App.ORDER_PRE_PAY_PRICED);
		List<String> listExpress = new ArrayList<>();

		System.out.println(specialExpresses.size());
		String content = "尊有效敬的客户您好，点此链接完成支付：。此链接五分钟之内。";
		for (Express e : specialExpresses){
			System.out.println(e.getShop().getId());
			Set<String> set = new HashSet<String>();
			set.add(e.getShop().getId());
			//发送短信
			HttpUtil.sendSMSToUserId(content, set);
			listExpress.add(e.getExpressNo());
		}
		String str = JSON.toJSON(listExpress).toString();
		redisCache.set(App.SPECIAL_EXPRESSES,3600*24,str);
		System.out.println("sendWarning");
	}

	public void chargeBack(){
		List<String> listExpress;
		listExpress = JSONObject.parseArray(redisCache.getString(App.SPECIAL_EXPRESSES),String.class);
		if (listExpress != null){
			//生成交易号
			JSONObject jsonObject = orderService.pay(listExpress,"wdFDuAk7ZhQ0i8ZceScTASqQ");
			//根据交易号请求余额支付
			HttpUtil.balancePay(jsonObject.getString("tranNo"));
			System.out.println("chargeBack");
		}

	}

}
