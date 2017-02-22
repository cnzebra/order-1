package com.mrwind.order.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
	
	public Integer updateExpressLineIndex(String expressNo, Integer lineIndex) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		query.addCriteria(Criteria.where("lines.index").is(lineIndex));
		Update update = Update.update("currentLine", lineIndex+1);
		update.set("lines.$.realTime", Calendar.getInstance().getTime());
		update.set("updateTime",Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategory(Express express) {
		Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		update.set("updateTime",Calendar.getInstance().getTime());
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
		update.set("updateTime",Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
	
	public int updateStatus(String expressNo, String status, String subStatus,Date createTime) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status).set("subStatus", subStatus).set("createTime", createTime);
		update.set("updateTime",Calendar.getInstance().getTime());
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
		update.set("updateTime",Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateExpressPlanEndTime(String expressNo, Date planEndTime) {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("planEndTime", planEndTime);
		update.set("updateTime",Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategoryAndStatus(Express express) {
		Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		update.set("status", express.getStatus()).set("subStatus", express.getSubStatus());
		update.set("updateTime",Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
	
	public synchronized void updateExpress(final Express express) {
		if(express==null)return;
		Runnable runnable = new Runnable() {
			public void run() {
				Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
				Update update =new Update();
				if (express.getCategory() != null) {
					update.set("category", express.getCategory());
				}

				if (StringUtils.isNotBlank(express.getStatus())) {
					update.set("status", express.getStatus());
				}

				if (StringUtils.isNotBlank(express.getSubStatus())) {
					update.set("subStatus", express.getSubStatus());
				}

				if (StringUtils.isNotBlank(express.getMode())) {
					update.set("mode", express.getMode());
				}

				if (StringUtils.isNotBlank(express.getRemark())) {
					update.set("remark", express.getRemark());
				}

				if (express.getBindExpressNo() != null) {
					update.set("bindExpressNo", express.getBindExpressNo());
				}

				if (express.getCurrentLine() != null) {
					update.set("currentLine", express.getCurrentLine());
				}

				if (express.getReceiver() != null) {
					update.set("receiver",express.getReceiver());
				}

				if (express.getShop() != null) {
					update.set("shop",express.getShop());
				}

				if (express.getSender() != null) {
					update.set("sender",express.getSender());
				}
				
				if(express.getCreateTime()!=null){
					update.set("createTime",express.getCreateTime());
				}

				if(express.getDownMoney() !=null){
					update.set("downMoney", express.getDownMoney() );
				}
				
				if(express.getPlanEndTime() !=null){
					update.set("planEndTime", express.getPlanEndTime());
				}
				
				if(express.getRealEndTime() != null){
					update.set("realEntTime", express.getRealEndTime());
				}

				update.set("updateTime",Calendar.getInstance().getTime());
				mongoTemplate.updateFirst(query, update, Express.class).getN();
			}
		};
		newSingleThreadExecutor.execute(runnable);
	}
}
