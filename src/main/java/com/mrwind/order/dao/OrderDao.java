package com.mrwind.order.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.Status;

@Repository
public class OrderDao extends BaseDao {

	public boolean updateOrderStatus(Long orderNumber, Status status) {

		mongoTemplate.updateFirst(Query.query(Criteria.where("orderNumber").is(orderNumber)),
				Update.update("status", status), Order.class);
		return true;
	}
	
	public boolean updateOrderStatusFence(Long orderNumber, Status status,Fence fence) {

		
		Update update = new Update();
		update.set("fence", fence);
		update.set("status", status);
		mongoTemplate.updateFirst(Query.query(Criteria.where("orderNumber").is(orderNumber)),
				update, Order.class);
		return true;
	}
}
