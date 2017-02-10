package com.mrwind.order.repositories;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.mrwind.order.entity.Order;

public interface OrderRepository extends CrudRepository<Order, Long>,QueryByExampleExecutor<Order>{

}
