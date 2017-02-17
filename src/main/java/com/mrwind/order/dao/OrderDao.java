package com.mrwind.order.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;

@Repository
public class OrderDao extends BaseDao {

	public boolean updateOrderStatus(Long orderNumber, String status,String subStatus) {

		mongoTemplate.updateFirst(Query.query(Criteria.where("orderNumber").is(orderNumber)),
				Update.update("status", status), Order.class);
		return true;
	}
	
	public boolean updateOrderStatusFence(Long orderNumber, String status,String subStatus,Fence fence) {
		Update update = new Update();
		update.set("fence", fence);
		update.set("status", status);
		update.set("subStatus", subStatus);
		mongoTemplate.updateFirst(Query.query(Criteria.where("orderNumber").is(orderNumber)),
				update, Order.class);
		return true;
	}
}
