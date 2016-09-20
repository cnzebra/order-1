package com.mrwind.order.repositories;


import org.springframework.data.repository.CrudRepository;

import com.mrwind.order.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Long>{

	Order findByNumber(Long Number);
	
	Order findByType(String type);
}
