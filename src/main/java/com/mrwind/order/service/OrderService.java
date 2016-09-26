package com.mrwind.order.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.order.App;
import com.mrwind.order.entity.Call;
import com.mrwind.order.entity.Order;
import com.mrwind.order.repositories.CallRepository;
import com.mrwind.order.repositories.OrderRepository;
import com.mrwind.order.repositories.UserRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RedisCache redisCache;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	

	@Autowired
	private CallRepository callRepository;

	public Order insert(Order order) {
		Long pk = redisCache.getPK("order", 1);
		order.setNumber(pk);
		return orderRepository.save(order);
	}

	public Order select(Order order) {
		Order findOne = orderRepository.findOne(order.getNumber());
		return findOne;
	}

	public Call saveCall(Call call) {
		Date sysDate = Calendar.getInstance().getTime();
		call.setCreateTime(sysDate);
		call.setStatus(App.CALL_CREATE);
		Call save = callRepository.save(call);
		return save;
	}

	public Call queryCallInfo(String id){
		return callRepository.findOne(id);
	}
	
	public boolean existsCall(String id){
		return callRepository.exists(id);
	}
	
	public Page<Call> queryCallList(Pageable pageable){
		return callRepository.findAll(pageable);
	}

	public void cancelOrder(JSONObject json) {
		
	}
}
