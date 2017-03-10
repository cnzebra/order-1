package com.mrwind.order.repositories;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;

import com.mrwind.order.entity.OrderReceipt;

public interface OrderReceiptRepository extends CrudRepository<OrderReceipt, ObjectId> {

	List<OrderReceipt> findAllByTranNo(String tranNo);
	
	List<OrderReceipt> findDistinctTranNoByExpressNoIn(Collection<String> express);
}
