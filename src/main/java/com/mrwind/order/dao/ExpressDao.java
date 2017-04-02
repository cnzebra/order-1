package com.mrwind.order.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.common.util.DateUtils;
import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.vo.MapExpressVO;
import com.mrwind.order.entity.vo.ShopExpressVO;

@Repository
public class ExpressDao extends BaseDao {

	ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

	public List<Express> findByTypeAndStatusAndSubStatus(String type, String status, String subStatus) {
		Query query = new Query();
		query.addCriteria(Criteria.where("type").is(type));
		query.addCriteria(Criteria.where("status").is(status));
		query.addCriteria(Criteria.where("subStatus").is(subStatus));
		return mongoTemplate.find(query, Express.class);
	}
	
	public List<Express> findRelationship(String userId) {
		Query query = Query.query(Criteria.where("lines.executorUser._id").is(userId));
		query.with(new Sort(Sort.Direction.DESC, "createTime"));
		return mongoTemplate.find(query, Express.class);
	}
	
	public Integer updateExpressLineIndex(String expressNo, Integer oldLineIndex, Integer newLineIndex) {
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

	public int replaceLine(Integer startIndex, List<Line> lines, String expressNo){
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		String cmd = "{ '$pull' : {'lines':{ 'index' : {'$gte':{ " + startIndex +"}}}}})";
		BasicUpdate basicUpdate = new BasicUpdate(cmd);
		mongoTemplate.updateFirst(query, basicUpdate, Express.class);
		return addLines(expressNo, lines);
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
		if (dueTime != null) {
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

	public int updateSubStatus(String expressNo, String subStatus) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("subStatus", subStatus);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public List<Express> findUnBegin(Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(App.ORDER_CREATE));
		query.addCriteria(Criteria.where("dueTime").gte(date));
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
				if (express.getEndAddress() != null) {
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

	public int updateExpressPrinted(List<String> expressNo) {
		Query query = Query.query(Criteria.where("expressNo").in(expressNo));
		Update update = Update.update("printed", true);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateMulti(query, update, Express.class).getN();
	}

	public Page<Express> selectByShopIdAndMode(String id, String status, String tel, String expressNo, Date date, PageRequest page) {
		Criteria operator = new Criteria();
		Query query = Query.query(Criteria.where("shop.id").is(id));
		if (StringUtils.isNotBlank(tel)) {
			operator.orOperator(Criteria.where("sender.tel").regex(tel), Criteria.where("receiver.tel").regex(tel));
		}
		if (StringUtils.isNotBlank(expressNo)) {
			query.addCriteria(Criteria.where("expressNo").is(expressNo));
		}
		if (date != null) {
			query.addCriteria(Criteria.where("dueTime").gte(date).lt(DateUtils.addDays(date, 1)));
		}
		if (StringUtils.isNotBlank(status)) {
			query.addCriteria(Criteria.where("status").is(status));
		}
		query.addCriteria(operator);
		query.with(page);
		long count = mongoTemplate.count(query, Express.class);
		return new PageImpl<>(mongoTemplate.find(query, Express.class), page, count);

	}

	public List<Express> selectByShopIdAndModeForWeChat(String id, String status, Date date, String dayType,
			String param, PageRequest page) {
		Criteria operator = new Criteria();
		Query query = Query.query(Criteria.where("shop.id").is(id));
		if (status != null) {
			query.addCriteria(Criteria.where("status").is(status));
		}
		if (StringUtils.isNotBlank(dayType)) {
			Date dayStart = DateUtils.getStartTime();
			Date dayEnd = DateUtils.getEndTime();
			if ("today".equals(dayType)) {
				query.addCriteria(Criteria.where("createTime").gte(dayStart).lt(dayEnd));
			} else if ("history".equals(dayType)) {
				query.addCriteria(Criteria.where("createTime").lt(dayStart));
			}
		}
		if (date != null) {
			query.addCriteria(Criteria.where("createTime").gte(date).lt(DateUtils.addDays(date, 1)));
		}
		if (StringUtils.isNotBlank(param)) {
			operator.orOperator(Criteria.where("expressNo").regex(param), Criteria.where("bindExpressNo").regex(param),
					Criteria.where("sender.name").regex(param), Criteria.where("sender.tel").regex(param),
					Criteria.where("receiver.tel").regex(param), Criteria.where("receiver.name").regex(param));
		}
		query.addCriteria(operator);
		query.with(page);
		return mongoTemplate.find(query, Express.class);
	}

	public List<ShopExpressVO> selectShopExpress(String shopId, PageRequest page) {
		ObjectId objectId = new ObjectId(shopId);
		Query query = new Query();
		query.addCriteria(Criteria.where("shop._id").is(objectId));
		query.addCriteria(Criteria.where("status").is(App.ORDER_BEGIN));
		query.fields().include("expressNo");
		query.fields().include("receiver");
		query.fields().include("remark");
		query.fields().include("bindExpressNo");
		query.fields().include("dueTime");
		query.with(page);
		return mongoTemplate.find(query, ShopExpressVO.class, "express");
	}

	public List<MapExpressVO> findAll(PageRequest pageRequest) {
		Query query = new Query();
		query.fields().include("expressNo");
		query.fields().include("receiver");
		query.fields().include("sender");
		query.fields().include("bindExpressNo");
		query.fields().include("dueTime");
		query.with(pageRequest);
		return mongoTemplate.find(query, MapExpressVO.class, "express");
	}

	public Express judgeBind(String tel, String shopId){
		Query query = new Query();
		query.addCriteria(Criteria.where("shop._id").is(new ObjectId(shopId)));
		query.addCriteria(Criteria.where("receiver.tel").is(tel));
		query.addCriteria(Criteria.where("bindExpressNo").is(""));
		return mongoTemplate.findOne(query, Express.class);
	}
}
