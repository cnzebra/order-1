package com.mrwind.order.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.common.util.DateUtils;
import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;

@Repository
public class ExpressDao extends BaseDao {

	ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

	public List<Express> findByTypeAndStatusAndSubStatus(String type,String status,String subStatus){
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(type));
		query.addCriteria(Criteria.where("status").is(status));
		query.addCriteria(Criteria.where("subStatus").is(subStatus));
		return mongoTemplate.find(query,Express.class);
	}
	
	public Integer updateExpressLineIndex(String expressNo, Integer oldLineIndex,Integer newLineIndex) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		query.addCriteria(Criteria.where("lines.index").is(oldLineIndex));
		Update update = Update.update("currentLine", newLineIndex);
		update.set("lines.$.realTime", Calendar.getInstance().getTime());
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategory(Express express) {
		Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		update.set("updateTime", Calendar.getInstance().getTime());
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

	public List<Express> findExpress(String param, String fenceName, String mode, String status, String day,
			Date dueTime, PageRequest page) {
		Criteria operator = new Criteria();
		if (StringUtils.isNotBlank(param)) {
			operator.orOperator(Criteria.where("bindExpressNo").regex(param), Criteria.where("shop.name").regex(param),
					Criteria.where("expressNo").regex(param));
		}
		List<Criteria> list = new ArrayList<>();
		if (StringUtils.isNotBlank(fenceName)) {
			list.add(Criteria.where("sender.fence.name").is(fenceName));
		}
		if (StringUtils.isNotBlank(mode)) {
			list.add(Criteria.where("mode").is(mode));
		}
		if (StringUtils.isNotBlank(status)) {
			list.add(Criteria.where("status").is(status));
		}
		if (StringUtils.isNotBlank(day)) {
			Date toDay = DateUtils.getStartTime();
			if (day.equals("today")) {
				list.add(Criteria.where("createTime").gte(toDay));
			} else {
				Date yesterday = DateUtils.addDays(toDay, -1);
				list.add(Criteria.where("createTime").lte(toDay).gte(yesterday));
			}
		}
		if(dueTime!=null){
			list.add(Criteria.where("dueTime").gte(dueTime).lte(DateUtils.addDays(dueTime, 1)));
		}
		
		if (list.size() > 0) {
			Criteria[] criteria = new Criteria[list.size()];
			operator.andOperator(list.toArray(criteria));
		}

		Query query = Query.query(operator);
		query.with(page);
		return mongoTemplate.find(query, Express.class);
	}

	public int updateStatus(String expressNo, String status, String subStatus) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status).set("subStatus", subStatus);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public List<Express> findUnBegin(Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(App.ORDER_CREATE));
		query.addCriteria(Criteria.where("dueTime").lte(date));
		return mongoTemplate.find(query, Express.class);
	}

	public int updateExpressBindNo(String expressNo, String bindExpressNo) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("bindExpressNo", bindExpressNo);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateExpressPlanEndTime(String expressNo, Date planEndTime) {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("planEndTime", planEndTime);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateCategoryAndStatus(Express express) {
		Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
		Update update = Update.update("category", express.getCategory());
		update.set("status", express.getStatus()).set("subStatus", express.getSubStatus());
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public synchronized void updateExpress(final Express express) {
		if (express == null)
			return;
		Runnable runnable = new Runnable() {
			public void run() {
				Query query = Query.query(Criteria.where("expressNo").is(express.getExpressNo()));
				Update update = new Update();
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

				if (express.getLines() != null) {
					update.set("lines", express.getLines());
				}

				if (express.getReceiver() != null) {
					update.set("receiver", express.getReceiver());
				}

				if (express.getShop() != null) {
					update.set("shop", express.getShop());
				}

				if (express.getSender() != null) {
					update.set("sender", express.getSender());
				}

				if (express.getCreateTime() != null) {
					update.set("createTime", express.getCreateTime());
				}

				if (express.getDownMoney() != null) {
					update.set("downMoney", express.getDownMoney());
				}

				if (express.getPlanEndTime() != null) {
					update.set("planEndTime", express.getPlanEndTime());
				}

				if (express.getRealEndTime() != null) {
					update.set("realEntTime", express.getRealEndTime());
				}
				if(express.getEndAddress()!=null){
					update.set("endAddress", express.getEndAddress());
				}

				update.set("updateTime", Calendar.getInstance().getTime());
				mongoTemplate.updateFirst(query, update, Express.class).getN();
			}
		};
		newSingleThreadExecutor.execute(runnable);
	}

	public int updateLines(String expressNo, List<Line> newList, Integer currentLine) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("lines", newList).set("currentLine", currentLine);
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}
}
