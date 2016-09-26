package com.mrwind.order.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.Status;

public class OrderDao extends BaseDao {

	public boolean updateOrderStatus(Long orderNumber, Status status) {

		mongoTemplate.updateFirst(Query.query(Criteria.where("orderNumber").is(orderNumber)),
				Update.update("status", status), Order.class);

		return true;
	}

}
