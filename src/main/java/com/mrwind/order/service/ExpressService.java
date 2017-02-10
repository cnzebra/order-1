package com.mrwind.order.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.OrderBase;
import com.mrwind.order.repositories.ExpressRepository;

@Service
public class ExpressService {

	@Autowired
	RedisCache redisCache;

	@Autowired
	ExpressRepository expressRepository;

	@Autowired
	ExpressDao expressDao;

	public Express initExpress(OrderBase order) {
		Express express = new Express(order);
		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk);
		express.setDuiTime(Calendar.getInstance().getTime());
		expressRepository.save(express);
		sendExpressLog21010(express);
		return express;
	}

	public Express initExpress(Express express) {
		Long pk = redisCache.getPK("express", 1);
		express.setExpressNo(pk);
		expressRepository.save(express);
		sendExpressLog21003(express);
		return express;
	}

	public void sendExpressLog21003(final Express express) {
		// TODO Auto-generated method stub
		Thread thread = new Thread() {
			public void run() {
				JSONObject param = new JSONObject();
				if (express.getCurrentLineDetail() == null) {
					return;
				}
				param.put("creatorId", express.getCurrentLineDetail().getExecutorUser().getId());
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
		express.setExpressNo(pk);
		express.setDuiTime(duiDate);
		expressRepository.save(express);
		sendExpressLog21010(express);
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

	public JSONObject selectByExpress(Express express) {
		Example<Express> example = Example.of(express);
		Iterable<Express> all = expressRepository.findAll(example);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("data", all);
		return successJSON;
	}

	public void sendExpressLog21010(List<Express> express) {

		if (express == null || express.size() < 1)
			return;
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

	public Integer completeLine(String expressNo, Integer lineIndex) {
		return expressDao.updateExpressLineIndex(expressNo, lineIndex);
	}

	public Express selectByExpressNo(String expressNo) {
		Express express=expressRepository.findFirstByExpressNo(Long.valueOf(expressNo));
		return express;
	}

	public List<Express> selectByExpressNo(List<Long> express) {
		// TODO Auto-generated method stub
		List<Express> list= expressRepository.findByExpressNoIn(express);
		return list;
	}

}
