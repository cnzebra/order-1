package com.mrwind.order.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.common.util.DateUtils;
import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.ShopUser;
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

	public List<Express> findByStatusAndCreateTime(String status, Date operaTime) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(status));
		query.addCriteria(Criteria.where("operaTime").lt(operaTime));
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
		update.set("excutorId", list.get(list.size() - 1).getExecutorUser().getId());
		update.set("operaTime", new Date());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int addLines(String expressNo, List<Line> list) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = new Update();
		update.pushAll("lines", list.toArray());
		update.set("excutorId", list.get(list.size() - 1).getExecutorUser().getId());
		update.set("operaTime", new Date());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int removeLine(String expressNo, Integer lineIndex) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		String cmd = "{ '$pull' : {'lines':{ 'index' : " + lineIndex + "}}})";
		BasicUpdate basicUpdate = new BasicUpdate(cmd);
		return mongoTemplate.updateFirst(query, basicUpdate, Express.class).getN();
	}

	public int replaceLine(Integer startIndex, List<Line> lines, String expressNo) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		String cmd = "{ '$pull' : {'lines':{ 'index' : {'$gte':{ " + startIndex + "}}}}})";
		BasicUpdate basicUpdate = new BasicUpdate(cmd);
		mongoTemplate.updateFirst(query, basicUpdate, Express.class);
		return addLines(expressNo, lines);
	}

	public List<Express> findExpress(String param, String shopId, String fenceName, String mode, String status,
			String day, Date dueTime, PageRequest page) {
		Criteria operator = new Criteria();
		if (StringUtils.isNotBlank(param)) {
			operator.orOperator(Criteria.where("bindExpressNo").regex(param), Criteria.where("shop.name").regex(param),
					Criteria.where("expressNo").regex(param));
		}
		List<Criteria> list = new ArrayList<>();
		if (StringUtils.isNoneBlank(shopId)) {
			if (ObjectId.isValid(shopId)) {
				list.add(Criteria.where("shop.id").is(new ObjectId(shopId)));
			} else {
				list.add(Criteria.where("shop.id").is(shopId));
			}
		}
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


	public List<Express> findAppExpress(String param, String shopId, Date time, PageRequest page) {
		Criteria operator = new Criteria();

		if (StringUtils.isNotBlank(param)) {
			operator.orOperator(
					Criteria.where("bindExpressNo").regex(param),
					Criteria.where("receiver.name").regex(param),
					Criteria.where("expressNo").regex(param),
					Criteria.where("receiver.tel").regex(param),
					Criteria.where("receiver.address").regex(param)
					);
		}
		List<Criteria> list = new ArrayList<>();
		if (StringUtils.isNoneBlank(shopId)) {
			if (ObjectId.isValid(shopId)) {
				list.add(Criteria.where("shop.id").is(new ObjectId(shopId)));
			} else {
				list.add(Criteria.where("shop.id").is(shopId));
			}
		}
		if (time != null) {
			list.add(Criteria.where("dueTime").gte(time).lte(DateUtils.addDays(time, 1)));
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
	
	public int updateStatus(String expressNo, String status) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updatePush(String expressNo, String status, String isPush) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("status", status);
		update.set("updateTime", Calendar.getInstance().getTime());
		update.set("isPush", isPush);
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
	
	public List<Express> findWaitComplete(Date date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(App.ORDER_WAIT_COMPLETE));
		query.addCriteria(Criteria.where("realEndTime").lte(date));
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
					List<Line> lines = express.getLines();
					update.set("lines", lines);
					update.set("excutorId", lines.get(lines.size() - 1).getExecutorUser().getId());
					update.set("operaTime", new Date());

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
					update.set("realEndTime", express.getRealEndTime());
				}
				if (express.getEndAddress() != null) {
					update.set("endAddress", express.getEndAddress());
				}
				if(express.getEndType()!=null){
					update.set("endType", express.getEndType());
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
		update.set("excutorId", newList.get(newList.size() - 1).getExecutorUser().getId());
		update.set("operaTime", new Date());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateExpressPrinted(List<String> expressNo) {
		Query query = Query.query(Criteria.where("expressNo").in(expressNo));
		Update update = Update.update("printed", true);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateMulti(query, update, Express.class).getN();
	}

	public int updateExpressLook(String expressNo, String isLook) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("isPush", isLook);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateMulti(query, update, Express.class).getN();
	}

	public int updateExpressDel(String expressNo, boolean isDelete) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("isDelete", isDelete);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateMulti(query, update, Express.class).getN();
	}

	public Page<Express> selectByShopIdAndMode(String shopId, String status, String tel, String expressNo, Date date,
			PageRequest page) {
		Criteria operator = new Criteria();
		Query query = new Query();
		if (ObjectId.isValid(shopId)) {
			query.addCriteria(Criteria.where("shop.id").is(new ObjectId(shopId)));
		} else {
			query.addCriteria(Criteria.where("shop.id").is(shopId));
		}
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
			if("noPrint".equals(status)){
				query.addCriteria(Criteria.where("printed").is(false));
			} else if("print".equals(status)){
				query.addCriteria(Criteria.where("printed").is(true));
			} else {
				query.addCriteria(Criteria.where("status").is(status));
			}

		}
		query.addCriteria(operator);
		query.with(page);
		long count = mongoTemplate.count(query, Express.class);
		return new PageImpl<>(mongoTemplate.find(query, Express.class), page, count);

	}

	public List<Express> selectByShopIdAndModeForWeChat(String shopId, String status, Date date, String dayType,
			String param, PageRequest page) {
		Criteria operator = new Criteria();
		Query query = new Query();
		if (ObjectId.isValid(shopId)) {
			query.addCriteria(Criteria.where("shop.id").is(new ObjectId(shopId)));
		} else {
			query.addCriteria(Criteria.where("shop.id").is(shopId));
		}
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

	public Express judgeBind(String tel, String shopId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("shop._id").is(new ObjectId(shopId)));
		query.addCriteria(Criteria.where("receiver.tel").is(tel));
		query.addCriteria(Criteria.where("status").ne(App.ORDER_COMPLETE));
		Date today = DateUtils.getStartTime();
		query.addCriteria(Criteria.where("dueTime").gte(today).lt(DateUtils.addDays(today, 1)));
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("bindExpressNo").is(""), Criteria.where("bindExpressNo").is(null));
		query.addCriteria(criteria);
		return mongoTemplate.findOne(query, Express.class);
	}

	public List<Express> selectCanBindExpress(String shopId, Date dueTime) {
		Query query = new Query();
		if (StringUtils.isNotBlank(shopId)) {
			if (ObjectId.isValid(shopId)) {
				query.addCriteria(Criteria.where("shop._id").is(new ObjectId(shopId)));
			} else {
				query.addCriteria(Criteria.where("shop.id").is(shopId));
			}
		}
		if (dueTime == null) {
			dueTime = DateUtils.getStartTime();
		}
		query.addCriteria(Criteria.where("dueTime").gte(dueTime).lt(DateUtils.addDays(dueTime, 1)));
		Criteria criteria = new Criteria();
		criteria.orOperator(Criteria.where("bindExpressNo").is(""), Criteria.where("bindExpressNo").is(null));
		query.addCriteria(criteria);
		Sort sort = new Sort(Direction.DESC, "createTime");
		query.with(sort);
		return mongoTemplate.find(query, Express.class);
	}

	/**
	 * 分页查询
	 * lines最后一条的executorUser的id固定
	 * receiver的name和tel匹配查询
	 *
     */
	public Page<Express> findExpress(String userId, String str, Integer pageNo, Integer pageSize, Point point, Double radius, String fliterStatus){

		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest page = new PageRequest(pageNo, pageSize, sort);
		Criteria operator = new Criteria();
		Query query = new Query();
		query.fields().slice("lines", -1);

		query.addCriteria(Criteria.where("excutorId").is(userId));
		if(StringUtils.isNotBlank(fliterStatus)){
			query.addCriteria(Criteria.where("status").nin(fliterStatus));
		}
		if(StringUtils.isNotBlank(str)){
			operator.orOperator(
					Criteria.where("receiver.tel").regex(str),
					Criteria.where("receiver.name").regex(str)) ;
		}
		if(point != null){
			query.addCriteria(Criteria.where("receiver.location").near(point).maxDistance(radius));
		}


		query.addCriteria(operator);
		query.with(page);
		long count = mongoTemplate.count(query, Express.class);
		return new PageImpl<>(mongoTemplate.find(query, Express.class), page, count);
	}

	public Page<Express> findExpress(String userId, Integer pageNo, Integer pageSize){

		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest page = new PageRequest(pageNo, pageSize, sort);
		Criteria operator = new Criteria();
		Query query = new Query();
		query.fields().slice("lines", -1);
		query.addCriteria(Criteria.where("excutorId").is(userId));
		query.addCriteria(Criteria.where("status").nin(App.ORDER_COMPLETE, App.ORDER_WAIT_COMPLETE));
		query.addCriteria(operator);
		query.with(page);
		long count = mongoTemplate.count(query, Express.class);
		return new PageImpl<>(mongoTemplate.find(query, Express.class), page, count);
	}

	public void saveExpressByBatch(final List<Express> expressList){
		Date time = Calendar.getInstance().getTime();
		for(Express express : expressList){
			express.setCreateTime(time);
			express.setUpdateTime(time);
		}
		mongoTemplate.insert(expressList, "express");
	}

	public int updateExpressReceiverAddress(String expressNo, String recevierName,String receiverAddress,Double lat,Double lng) {
		// TODO Auto-generated method stub
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("receiver.address", receiverAddress).set("receiver.name", recevierName);
		if(lat!=null){
			update.set("receiver.lat", lat);
			update.set("receiver.lng", lng);
		}
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public int updateExpressReminded(String expressNo) {
		Query query = Query.query(Criteria.where("expressNo").is(expressNo));
		Update update = Update.update("reminded", true);
		update.set("updateTime", Calendar.getInstance().getTime());
		return mongoTemplate.updateFirst(query, update, Express.class).getN();
	}

	public List<ShopUser> selectShopByReceiverTel(String tel) {
		Query query = new Query();
		query.fields().include("shop");
		query.addCriteria(Criteria.where("receiver.tel").is(tel));
		Date startTime = DateUtils.getStartTime();
		query.addCriteria(Criteria.where("dueTime").gte(startTime));
		return mongoTemplate.find(query, ShopUser.class, "express");
	}
}
