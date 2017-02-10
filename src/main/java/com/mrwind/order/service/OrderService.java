package com.mrwind.order.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.order.App;
import com.mrwind.order.dao.OrderDao;
import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired ExpressService express;
	
	@Autowired
	private OrderDao orderDao;

	public JSONObject insert(Order order) {
		order.setStatus(App.ORDER_CREATE);
		order.setSubStatus(App.ORDER_PRE_CREATED);
		
		order.setUpdateTime(Calendar.getInstance().getTime());
		if(order.getDuiTimes()==null||order.getDuiTimes().size()==0){
			express.initExpress(order);
			order.setStatus(App.ORDER_BEGIN);
		}
		orderRepository.save(order);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		return successJSON;
	}

	public Order selectById(Long orderNumber) {
		Order findOne = orderRepository.findOne(orderNumber);
		return findOne;
	}
	
	public void submitOrderPriced(Long orderNumber,Fence fence){
		orderDao.updateOrderStatusFence(orderNumber, App.ORDER_BEGIN,App.ORDER_BEGIN, fence);
	}

	public void completeOrder(Long orderNumber) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,App.ORDER_FINISHED);
	}
	
	public void errorCompleteOrder(Long orderNumber,String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,subStatus);
	}

	public boolean cancelOrder(Long orderNumber, String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_CANCLE,subStatus);
		return true;
	}

	public Order selectByOrder(Order order) {
		Example<Order> example=Example.of(order);
		return orderRepository.findOne(example);
	}
}
