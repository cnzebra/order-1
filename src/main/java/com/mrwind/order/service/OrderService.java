package com.mrwind.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mrwind.order.entity.Order;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	public void insert(Order order){
		orderRepository.save(order);
	}
	
	public Order select(Order order){
		Order findOne = orderRepository.findOne(order.getId());
		Order findByType = orderRepository.findByType(order.getType());
		return findByType;
	}

}
