package com.mrwind.order.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDao  {
	
	@Autowired
	protected MongoTemplate mongoTemplate;
}
