package com.mrwind.order.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.App;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Line;
import com.mrwind.order.entity.Line.LineUtil;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.User;
import com.mrwind.order.repositories.ExpressRepository;

@Service
public class ExpressService {

	@Autowired
	RedisCache redisCache;

	@Autowired
	ExpressRepository expressRepository;

	ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();

	@Autowired
	ExpressDao expressDao;

	public List<Express> createExpress(Order order) {
		List<Express> list = new ArrayList<>();
		if (order.getDuiTimes() == null || order.getDuiTimes().size() == 0) {
			list.add(initExpress(order, null));
			return list;
		}
		List<Date> duiTimes = order.getDuiTimes();
		System.out.println("这里需要一个人!");
		for (Date duiTime : duiTimes) {
			list.add(initExpress(order, duiTime));
		}
		return list;
	}

	public Express initExpress(Express express) {
		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk.toString());
		expressRepository.save(express);
		sendExpressLog21003(express);
		return express;
	}

	public void sendExpressLog21003(final Express express) {
		// TODO Auto-generated method stub
		Thread thread = new Thread() {
			public void run() {
				JSONObject param = new JSONObject();
				Line currentLine = LineUtil.getLine(express.getLines(), express.getCurrentLine());
				if (currentLine == null) {
					return;
				}
				param.put("creatorId", currentLine.getExecutorUser().getId());
				param.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				param.put("type", "21003");
				param.put("orderId", express.getExpressNo());
				param.put("orderSum", express.getCategory().getTotalPrice());

				JSONObject caseDetail = new JSONObject();
				caseDetail.put("shopId", express.getShop().getId());
				caseDetail.put("receiver", express.getReceiver());
				caseDetail.put("sender", express.getSender());
				param.put("caseDetail", caseDetail);
				HttpUtil.sendWindDataLog(param);
			}
		};
		thread.start();
	}

	public Express initExpress(Order order, Date duiDate) {
		// TODO Auto-generated method stub
		Express express = new Express(order);
		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk.toString());
		if (duiDate == null) {
			duiDate = Calendar.getInstance().getTime();
		}
		express.setDuiTime(duiDate);
		if (DateUtils.pastMinutes(duiDate) >= -60 * 2) {
			express.setStatus(App.ORDER_BEGIN);
			express.setSubStatus(App.ORDER_PRE_CREATED);
			sendExpressLog21010(express);
		}
		expressRepository.save(express);
		return express;
	}

	public void sendExpressLog21010(final Express express) {
		Thread thread = new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				json.put("type", "21010");
				json.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				json.put("shopId", express.getShop().getId());
				List<JSONObject> caseDetailList = new ArrayList<>();
				JSONObject caseDetail = new JSONObject();
				caseDetail.put("orderId", express.getExpressNo());
				caseDetail.put("shopId", express.getShop().getId());
				caseDetail.put("shopName", express.getShop().getName());
				caseDetail.put("shopTel", express.getShop().getTel());
				caseDetail.put("receiver", express.getReceiver());
				caseDetail.put("orderUserType", express.getOrderUserType());
				caseDetail.put("dueTime", express.getDuiTime());
				caseDetail.put("sender", express.getSender());
				caseDetailList.add(caseDetail);
				json.put("caseDetail", caseDetailList);
				HttpUtil.sendWindDataLog(json);
			}
		};
		thread.start();

	}

	public Express selectByExpress(Express express) {
		Example<Express> example = Example.of(express);
		return expressRepository.findOne(example);
	}

	public void sendExpressLog21010(final List<Express> express) {
		if (express == null || express.size() < 1)
			return;
		Thread thread = new Thread() {
			public void run() {
				JSONObject json = new JSONObject();
				json.put("type", "21010");
				json.put("createTime", Calendar.getInstance().getTime());
				json.put("shopId", express.get(0).getShop().getId());
				Iterator<Express> iterator = express.iterator();
				List<JSONObject> caseDetailList = new ArrayList<>();
				while (iterator.hasNext()) {
					Express next = iterator.next();
					JSONObject caseDetail = new JSONObject();
					caseDetail.put("orderId", next.getExpressNo());
					caseDetail.put("shopId", next.getShop().getId());
					caseDetail.put("shopName", next.getShop().getName());
					caseDetail.put("shopTel", next.getShop().getTel());
					caseDetail.put("receiver", next.getReceiver());
					caseDetail.put("orderUserType", next.getOrderUserType());
					caseDetail.put("dueTime", next.getDuiTime());
					caseDetail.put("sender", next.getSender());
					caseDetailList.add(caseDetail);
				}
				json.put("caseDetail", caseDetailList);
				HttpUtil.sendWindDataLog(json);
			}
		};
		thread.start();
	}

	public Integer completeLine(String expressNo, Integer lineIndex) {
		return expressDao.updateExpressLineIndex(expressNo, lineIndex);
	}

	public Express selectByExpressNo(String expressNo) {
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		return express;
	}

	public List<Express> selectByExpressNo(List<String> express) {
		// TODO Auto-generated method stub
		List<Express> list = expressRepository.findByExpressNoIn(express);
		return list;
	}

	public JSONObject updateCategory(Express express) {
		Express firstExpress = expressRepository.findFirstByExpressNo(express.getExpressNo());
		if (firstExpress == null) {
			return JSONFactory.getErrorJSON("查无该订单");
		}
		firstExpress.setCategory(express.getCategory());
		firstExpress.setStatus(App.ORDER_BEGIN);
		firstExpress.setSubStatus(App.ORDER_PRE_PRICED);
		updateExpress(express);
		return JSONFactory.getSuccessJSON();
	}

	public synchronized JSONObject updateExpress(final Express express) {
		Runnable runnable = new Runnable() {
			public void run() {
				Express firstExpress = expressRepository.findFirstByExpressNo(express.getExpressNo());
				if (firstExpress == null) {
					return;
				}
				if (express.getCategory() != null) {
					firstExpress.setCategory(express.getCategory());
				}

				if (StringUtils.isNotBlank(express.getStatus())) {
					firstExpress.setStatus(App.ORDER_BEGIN);
				}

				if (StringUtils.isNotBlank(express.getSubStatus())) {
					firstExpress.setSubStatus(App.ORDER_PRE_PRICED);
				}

				if (StringUtils.isNotBlank(express.getOrderUserType())) {
					firstExpress.setOrderUserType(express.getOrderUserType());
				}

				if (StringUtils.isNotBlank(express.getRemark())) {
					firstExpress.setRemark(express.getRemark());
				}

				if (express.getBindExpressNo() != null) {
					firstExpress.setBindExpressNo(express.getBindExpressNo());
				}

				if (express.getCurrentLine() != null) {
					firstExpress.setCurrentLine(express.getCurrentLine());
				}

				if (express.getLines() != null) {
					firstExpress.setLines(express.getLines());
				}

				if (express.getDuiTime() != null) {
					firstExpress.setDuiTime(express.getDuiTime());
				}

				if (express.getReceiver() != null) {
					firstExpress.setReceiver(express.getReceiver());
				}

				if (express.getShop() != null) {
					firstExpress.setShop(express.getShop());
				}

				if (express.getSender() != null) {
					firstExpress.setSender(express.getSender());
				}

				firstExpress.setUpdateTime(Calendar.getInstance().getTime());
				expressRepository.save(firstExpress);
			}
		};
		newSingleThreadExecutor.execute(runnable);
		return JSONFactory.getSuccessJSON();
	}

	public JSONObject errorComplete(List<String> list, JSONObject userInfo) {
		Iterator<String> iterator = list.iterator();
		User user = JSONObject.toJavaObject(userInfo, User.class);

		while (iterator.hasNext()) {
			String expressNo = iterator.next();
			Express express = expressRepository.findFirstByExpressNo(expressNo);
			List<Line> lines = express.getLines();

			Line line = new Line();
			line.setExecutorUser(user);
			line.setRealTime(Calendar.getInstance().getTime());
			line.setTitle(user.getName() + "异常妥投了订单");
			line.setIndex(lines.size());
			lines.add(line);
			express.setCurrentLine(lines.size());
			express.setLines(lines);
			express.setStatus(App.ORDER_COMPLETE);
			express.setSubStatus(App.ORDER_ERROR_COMPLETE);
			updateExpress(express);
		}
		return JSONFactory.getSuccessJSON();
	}

	public void addLine(String expressNo, List<Line> list) {
		Date beginTime = list.get(0).getPlanTime();
		System.out.println(beginTime);
		expressDao.addLines(expressNo, list);
	}

	public void removeLine(String expressNo, Integer lineIndex) {
		expressDao.removeLine(expressNo, lineIndex);
	}

	public void updateLine(String expressNo, List<Line> list) {
		expressDao.updateLines(expressNo, list);
	}

	public Page<Express> selectByShop(String shopId, Integer pageIndex, Integer pageSize) {
		// TODO Auto-generated method stub
		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest pageRequest = new PageRequest(pageIndex, pageSize, sort);
		if (ObjectId.isValid(shopId)) {
			ObjectId objectId = new ObjectId(shopId);
			return expressRepository.findByShopId(objectId, pageRequest);
		}
		return expressRepository.findByShopId(shopId, pageRequest);
	}

	public int udpateExpressStatus(String expressNo, String status, String subStatus) {
		return expressDao.updateStatus(expressNo, status, subStatus);
	}

	public Page<Express> selectAllByExpress(Express express, Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC,"createTime");
		PageRequest page =new PageRequest(pageIndex, pageSize,sort);
		Example<Express> example=Example.of(express);
		return expressRepository.findAll(example,page);
	}
	
	public List<Express> selectAll(String param,String fenceName){
		return expressDao.findExpress(param, fenceName);
	}

	public void modifiLine(String expressNo, List<Line> list) {
		Express express = expressRepository.findFirstByExpressNo(expressNo);
		List<Line> lines = express.getLines();
		List<Line> newList=new ArrayList<>(list.size()+lines.size());
		newList.addAll(lines);
		for(Line line : list){
			newList.set(line.getIndex()-1, line);
		}
		newList.remove(null);
		expressDao.updateLines(expressNo, newList);
	}
}
