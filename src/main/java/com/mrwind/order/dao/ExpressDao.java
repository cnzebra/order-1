package com.mrwind.order.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;

@Repository
public class ExpressDao extends BaseDao {
	public Integer updateExpressLineIndex(String expressNo, Integer lineIndex) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		query.addCriteria(Criteria.where("lines.index").is(lineIndex));
		Update update = Update.update("currentLine", lineIndex+1);
		update.set("lines.$.realTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategory(Express express) {
		Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateLines(String expressNo, List<Line> list) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("lines", list);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int addLines(String expressNo, List<Line> list) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = new Update();
		update.pushAll("lines", list.toArray());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int removeLine(String expressNo, Integer lineIndex) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		String cmd = "{ '$pull' : {'lines':{ 'index' : " + lineIndex + "}}})";
		BasicUpdate basicUpdate = new BasicUpdate(cmd);
		return mongoTemplate.updateFirst(query, basicUpdate, Express.class).getN();
	}

	public List<Express> findExpress(String param, String fenceName) {
		Criteria operator = new Criteria();
		operator.orOperator(Criteria.where("bindExpressNo").regex(param),Criteria.where("shop.name").regex(param),
				Criteria.where("expressNo").regex(param));
		if (StringUtils.isNotBlank(fenceName)) {
			operator.andOperator(Criteria.where("sender.fence.name").is(fenceName));
		}
		Query query = Query.query(operator);
		return mongoTemplate.find(query, Express.class);
	}

	public int updateStatus(String expressNo, String status, String subStatus) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status).set("subStatus", subStatus);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
	
	public int updateStatus(String expressNo, String status, String subStatus,Date date) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status).set("subStatus", subStatus).set("createTime", date);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public List<Express> findUnBegin(Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(App.ORDER_CREATE));
		query.addCriteria(Criteria.where("duiTime").gte(date));
		return mongoTemplate.find(query, Express.class);
	}

	public int updateExpressBindNo(String expressNo, String bindExpressNo) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("bindExpressNo", bindExpressNo);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateExpressPlanTime(String expressNo, Date planTime) {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("planTime", planTime);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
}
