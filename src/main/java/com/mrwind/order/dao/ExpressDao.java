package com.mrwind.order.dao;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Express;

@Repository
public class ExpressDao extends BaseDao {

	
	public Integer updateExpressLineIndex(String expressNo,Integer lineIndex){
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("currentLine", lineIndex);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
}
