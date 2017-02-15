package com.mrwind.order.repositories;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.mrwind.order.entity.OrderReceipt;

public interface OrderReceiptRepository extends CrudRepository<OrderReceipt, ObjectId> {

}
