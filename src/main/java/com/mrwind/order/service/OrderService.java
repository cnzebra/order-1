package com.mrwind.order.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mrwind.common.cache.RedisCache;
import com.mrwind.order.App;
import com.mrwind.order.entity.Call;
import com.mrwind.order.entity.Order;
import com.mrwind.order.repositories.CallRepository;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private CallRepository callRepository;

	public void insert(Order order) {
		orderRepository.save(order);
	}

	public Order select(Order order) {
		Order findOne = orderRepository.findOne(order.getId());
		return findOne;
	}

	public Call saveCall(Call call) {
		Date sysDate = Calendar.getInstance().getTime();
		Long callId = redisCache.getPK("call", 1);
		call.setId(callId);
		call.setCreateTime(sysDate);
		call.setStatus(App.CALL_CREATE);
		Call save = callRepository.save(call);
		return save;
	}

	public Call queryCallInfo(Long id){
		return callRepository.findOne(id);
	}
	
	public Page<Call> queryCallList(Pageable pageable){
		return callRepository.findAll(pageable);
	}
	
	
}
