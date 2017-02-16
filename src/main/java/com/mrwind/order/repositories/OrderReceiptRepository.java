package com.mrwind.order.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mrwind.order.entity.OrderReceipt;

public interface OrderReceiptRepository extends CrudRepository<OrderReceipt, String> {

}
