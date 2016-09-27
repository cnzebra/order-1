package com.mrwind.order.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mrwind.common.cache.RedisCache;
import com.mrwind.order.App;
import com.mrwind.order.dao.OrderDao;
import com.mrwind.order.entity.Call;
import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.Status;
import com.mrwind.order.repositories.CallRepository;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private RedisCache redisCache;
	
	@Autowired
	private OrderDao orderDao;

	@Autowired
	private CallRepository callRepository;

	public Order insert(Order order) {
		Long pk = redisCache.getPK("order", 1);
		order.setNumber(pk);
		Status status = new Status();
		status.setStatus(App.ORDER_BEGIN);
		status.setSubStatus(App.ORDER_PRE_CREATED);
		order.setStatus(status);
		
		return orderRepository.save(order);
	}

	public Order selectById(Long orderNumber) {
		Order findOne = orderRepository.findOne(orderNumber);
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
	
	public void submitOrderPriced(Long orderNumber,Fence fence){
		Status status = new Status();
		status.setStatus(App.ORDER_BEGIN);
		status.setSubStatus(App.ORDER_BEGIN);
		orderDao.updateOrderStatusFence(orderNumber, status, fence);
	}

	public void completeOrder(Long orderNumber) {
		Status status = new Status();
		status.setStatus(App.ORDER_COMPLETE);
		status.setSubStatus(App.ORDER_FINISHED);
		orderDao.updateOrderStatus(orderNumber, status);
	}
	
	public void errorCompleteOrder(Long orderNumber,String subStatus) {
		Status status = new Status();
		status.setStatus(App.ORDER_COMPLETE);
		status.setSubStatus(subStatus);
		orderDao.updateOrderStatus(orderNumber, status);
	}

	public boolean cancelOrder(Long orderNumber, String subStatus) {
		Status status = new Status();
		status.setStatus(App.ORDER_CANCLE);
		status.setSubStatus(subStatus);
		orderDao.updateOrderStatus(orderNumber, status);
		return true;
	}
}
