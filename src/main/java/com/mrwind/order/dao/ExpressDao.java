package com.mrwind.order.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;

@Repository
public class ExpressDao extends BaseDao {

	
	public Integer updateExpressLineIndex(String expressNo,Integer lineIndex){
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("currentLine", lineIndex);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategory(Express express) {
		Query query=Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateLines(Long expressNo, List<Line> list) {
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("lines", list);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
	
	public int addLines(Long expressNo, List<Line> list) {
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = new Update();
		update.push("lines", list);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
	
	public int removeLine(Long expressNo,Integer lineIndex){
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = new Update();
		update.pull("lines","index:{$gt:"+lineIndex+"}");
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateStatus(Long expressNo,String status, String subStatus) {
		Query query=Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status).set("subStatus", subStatus);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
}
