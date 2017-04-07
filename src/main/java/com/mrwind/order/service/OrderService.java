package com.mrwind.order.service;

import static com.mrwind.common.constant.ConfigConstant.API_WECHAT_HOST;

import java.math.BigDecimal;
import java.util.*;

import com.mrwind.common.util.Md5Util;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.User;

import com.mrwind.order.repositories.ExpressRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.factory.JSONFactory;
import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.common.util.SetUtil;
import com.mrwind.common.util.UUIDUtils;
import com.mrwind.order.App;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.OrderReceipt;
import com.mrwind.order.repositories.OrderReceiptRepository;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class OrderService {

	static Logger log = Logger.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	ExpressService expressService;

	@Autowired
	OrderReceiptRepository orderReceiptRepository;

	@Autowired
	RedisCache redisCache;

	@Autowired
	ExpressDao expressDao;

	public JSONObject reminder(String expressNo) {
		// 根据运单号查找当前派送人
		Express express = expressService.selectByNo(expressNo);
		User user = express.getLines().get(express.getCurrentLine() - 1).getExecutorUser();
		// 推送派送人手机
		JSONObject phoneJson = new JSONObject();
		phoneJson.put("uid", user.getId());
		JSONObject contentJson = new JSONObject();
		contentJson.put("title", "催单任务提醒");
		contentJson.put("content", "运单" + expressNo + "被催单，请优先配送！");
		phoneJson.put("data", contentJson);
		HttpUtil.pushJsonDateToPhone(phoneJson);
		return JSONFactory.getSuccessJSON();
	}

	@Autowired
	ExpressRepository expressRepository;

	public List<Express> initAndInsert(Order order) {
		Order resOrder = save(order);
		return expressService.createExpress(resOrder);
	}

	public List<Express> initAndInsert(List<Order> list) {
		List<Order> newList = new ArrayList<>();
		for (Order order : list) {
			Order save = save(order);
			newList.add(save);
		}
		System.out.println(System.currentTimeMillis());
		return expressService.createExpress(newList);
	}

	public Order selectByOrder(Order order) {
		Example<Order> example = Example.of(order);
		return orderRepository.findOne(example);
	}

	public Page<Order> selectAllByOrder(Order order, Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC, "createTime");
		PageRequest page = new PageRequest(pageIndex, pageSize, sort);

		Example<Order> example = Example.of(order);
		return orderRepository.findAll(example, page);
	}

	public synchronized JSONObject pay(List<String> listExpress, String userId) {
		List<Express> list = expressService.selectByExpressNo(listExpress);
		if (list.size() == 0) {
			return JSONFactory.getErrorJSON("查找不到订单，无法支付!");
		}

		List<OrderReceipt> listReceipt = new ArrayList<>();
		Iterator<Express> iterator = list.iterator();
		String tranNo = UUIDUtils.getUUID();
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal totalDownPrice = BigDecimal.ZERO;
		BigDecimal totalServicePrice = BigDecimal.ZERO;
		String shopId = "";
		int i = 0;
		JSONArray jsonArray = new JSONArray();
		while (iterator.hasNext()) {
			Express next = iterator.next();
			if (App.ORDER_TYPE_AFTER.equals(next.getType())) {
				continue;
			}
			if (next.getSubStatus().equals(App.ORDER_PRE_CREATED)) {
				return JSONFactory.getErrorJSON("未定价，订单号:" + next.getExpressNo()
						+ (next.getBindExpressNo() == null ? "。" : ("，绑定单号为:" + next.getBindExpressNo())));
			}
			if (!next.getStatus().equals(App.ORDER_BEGIN)) {
				return JSONFactory.getErrorJSON("订单当前状态无法支付，订单号为:" + next.getExpressNo()
						+ (next.getBindExpressNo() == null ? "。" : ("，绑定单号为:" + next.getBindExpressNo())));
			}
			// if(redisCache.hget(App.RDKEY_PAY_ORDER,
			// next.getExpressNo().toString())!=null){ 改为更新交易号的方式
			// return JSONFactory.getErrorJSON("订单正在支付，无法重复发起支付!");
			// }
			if (next.getShop() != null) {
				if (shopId.equals("")) {
					shopId = next.getShop().getId();
				}
				if (!shopId.equals(next.getShop().getId())) {
					return JSONFactory.getErrorJSON("发送的订单数据异常，不属于同一个商户，无法支付");
				}
			}
			OrderReceipt orderReceipt = new OrderReceipt(next);
			if (i != 0) {
				totalServicePrice.add(orderReceipt.getCategory().getServicePrice());
			} else {
				i++;
			}
			totalPrice = totalPrice.add(orderReceipt.getPrice());
			if (next.getDownMoney() != null) {
				totalDownPrice = totalDownPrice.add(next.getDownMoney());
			}
			if (jsonArray.size() > 0) {
				HttpUtil.compileExpressMission(jsonArray);
			}

			Object key = redisCache.hget(App.RDKEY_PAY_ORDER, next.getExpressNo().toString());
			if (key != null) {
				String strKey = key.toString();
				redisCache.hdel(App.RDKEY_PAY_ORDER.getBytes(), next.getExpressNo().getBytes());
				redisCache.delete("transaction_" + strKey);
			}
			redisCache.hset(App.RDKEY_PAY_ORDER, next.getExpressNo(), tranNo);
			orderReceipt.setTranNo(tranNo);
			listReceipt.add(orderReceipt);
		}

		orderReceiptRepository.save(listReceipt);

		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("content", listReceipt);
		successJSON.put("totalPrice", totalPrice.subtract(totalDownPrice).subtract(totalServicePrice));
		successJSON.put("expressCount", i);
		successJSON.put("totalDownPrice", totalDownPrice);
		successJSON.put("tranNo", tranNo);
		successJSON.put("shopId", shopId);
		if (StringUtils.isNoneEmpty(userId)) {
			successJSON.put("executor", HttpUtil.findPrivateUserInfo(userId));
		}

		redisCache.set("transaction_" + tranNo, 3600, successJSON.toJSONString());
		return successJSON;
	}

	public JSONObject payCredit(List<String> listExpress, String userId) {
		List<Express> list = expressService.selectByExpressNo(listExpress);
		if (list.size() == 0) {
			return JSONFactory.getErrorJSON("查找不到订单，无法支付!");
		}

		Iterator<Express> iterator = list.iterator();
		String shopId = "";
		String shopTel = "";
		while (iterator.hasNext()) {
			Express next = iterator.next();

			if (next.getSubStatus().equals(App.ORDER_PRE_CREATED)) {
				return JSONFactory.getErrorJSON("未定价，订单号:" + next.getExpressNo()
						+ (StringUtils.isBlank(next.getBindExpressNo()) ? "。" : ("，绑定单号为:" + next.getBindExpressNo())));
			}
			if (!next.getStatus().equals(App.ORDER_BEGIN)) {
				return JSONFactory.getErrorJSON("订单当前状态无法支付，订单号为:" + next.getExpressNo()
						+ (StringUtils.isBlank(next.getBindExpressNo()) ? "。" : ("，绑定单号为:" + next.getBindExpressNo())));
			}

			if (next.getShop() != null) {
				if (shopId.equals("")) {
					shopId = next.getShop().getId();
				}
				if (shopTel.equals("")) {
					shopTel = next.getShop().getTel();
				}
				if (!shopId.equals(next.getShop().getId())) {
					return JSONFactory.getErrorJSON("发送的订单数据异常，不属于同一个商户，无法支付");
				}
			}
			next.setStatus(App.ORDER_SENDING);
			next.setSubStatus(App.ORDER_PRE_PAY_CREDIT);
			// expressService.updateLineIndex(next, 1);
			next.setCurrentLine(null); // 不要更新Index
			expressService.updateExpress(next);
			// 发送短信
			sendReceiveMessage(next.getSender().getName(), next.getReceiver().getTel(), next.getExpressNo());
		}
		if (list.size() > 0) {
			// sendExpressLog21004(express);
//			HttpUtil.findLineAndCreateMission(list);
		}

		Collection<String> tels = new HashSet<>();
		String content = "您的货物我们已经收到，即将开始为您配送，消费额会在今天21:00从余额中扣除，对该笔消费有疑问，欢迎致电：0571-28216560";
		tels.add(shopTel);
		HttpUtil.sendSMSToUserTel(content, SetUtil.ParseToString(tels));

		// HttpUtil.compileExpressMission(json);
		JSONObject successJSON = JSONFactory.getSuccessJSON();
		return successJSON;
	}

	/***
	 * 系统发起付款
	 * 
	 * @param expressNo
	 * @param string
	 * @return
	 */
	public JSONObject systemPay(Set<String> expressNo, String string) {
		// TODO Auto-generated method stub
		List<Express> list = expressService.selectByExpressNo(expressNo);
		if (list.size() == 0) {
			return null;
		}

		List<OrderReceipt> listReceipt = new ArrayList<>();
		Iterator<Express> iterator = list.iterator();
		String tranNo = UUIDUtils.getUUID();
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal totalDownPrice = BigDecimal.ZERO;
		String shopId = "";
		int i = 0;
		while (iterator.hasNext()) {
			Express next = iterator.next();

			if (next.getShop() != null) {
				if (shopId.equals("")) {
					shopId = next.getShop().getId();
				}
				if (!shopId.equals(next.getShop().getId())) {
					return null;
				}
			}

			OrderReceipt orderReceipt = new OrderReceipt(next);
			totalPrice = totalPrice.add(orderReceipt.getPrice());
			if (next.getDownMoney() != null) {
				totalDownPrice = totalDownPrice.add(next.getDownMoney());
			}
			orderReceipt.setTranNo(tranNo);
			listReceipt.add(orderReceipt);
			i++;
		}

		orderReceiptRepository.save(listReceipt);

		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("content", listReceipt);
		successJSON.put("totalPrice", totalPrice.subtract(totalDownPrice));
		successJSON.put("expressCount", i);
		successJSON.put("totalDownPrice", totalDownPrice);
		successJSON.put("tranNo", tranNo);
		successJSON.put("shopId", shopId);

		redisCache.set("transaction_" + tranNo, 3600, successJSON.toJSONString());
		return successJSON;
	}

	public JSONObject queryTranSactionDetail(String tranNo) {
		String successJSON = redisCache.getString("transaction_" + tranNo);
		if (StringUtils.isBlank(successJSON)) {
			return JSONFactory.getErrorJSON("交易已经关闭!");
		}
		return JSONObject.parseObject(successJSON);
	}

	public JSONObject lockOrder(String tranNo) {
		String res = redisCache.getString("transaction_" + tranNo);
		if (StringUtils.isEmpty(res)) {
			return JSONFactory.getErrorJSON("不用锁单了，请求号已经无效");
		}
		JSONObject jsonObject = JSONObject.parseObject(res);
		JSONArray items = jsonObject.getJSONArray("content");
		Iterator<Object> iterator = items.iterator();
		while (iterator.hasNext()) {
			OrderReceipt next = JSONObject.toJavaObject((JSONObject) iterator.next(), OrderReceipt.class);
			redisCache.hset(App.RDKEY_PAY_ORDER, next.getExpressNo().toString(), 1);
		}
		return JSONFactory.getSuccessJSON();
	}

	public String payCallback(String tranNo) {
		List<OrderReceipt> list = orderReceiptRepository.findAllByTranNo(tranNo);
		redisCache.delete("transaction_" + tranNo);
		log.info("付款回调:" + tranNo);
//		List<Express> expresses = Collections.emptyList();
		for (OrderReceipt orderReceipt : list) {
			redisCache.hdel(App.RDKEY_PAY_ORDER.getBytes(), orderReceipt.getExpressNo().toString().getBytes());
			if (App.ORDER_PRE_PAY_CREDIT.equals(orderReceipt.getPayType()))
				continue; // 后付款不处理
			expressService.udpateExpressStatus(orderReceipt.getExpressNo(), App.ORDER_SENDING,
					App.ORDER_PRE_PAY_PRICED);

			// expressService.updateLineIndex(orderReceipt.getExpressNo(), 1);
			sendReceiveMessage(orderReceipt.getSender().getName(), orderReceipt.getReceiver().getTel(), orderReceipt.getExpressNo());

//			expresses.add(expressRepository.findFirstByExpressNo(orderReceipt.getExpressNo()));
		}
//		if (expresses.size() > 0) {
			// TODO: 2017/3/22 此处应异步
//			HttpUtil.findLineAndCreateMission(expresses);
			// sendExpressLog21004(express);
//		}
		// HttpUtil.compileExpressMission(json);

		return null;
	}

	public void sendReceiveMessage(String sendName, String receiveTel, String expressNo) {
		// 发送短信
		if (StringUtils.isNotEmpty(sendName)) {
            String encode = Md5Util.string2MD5(expressNo + App.SESSION_KEY);
            String content = "【风先生】"+ sendName +"通过风先生为你发送了快件，物流详细信息请点击链接："+ API_WECHAT_HOST + "#/phone/orderTrace/"
                    + Md5Util.string2MD5(expressNo + App.SESSION_KEY);
            redisCache.set(encode, 60 * 60 * 24 * 15, expressNo);
            HttpUtil.sendSMSToUserTel(content, receiveTel);
        }
	}

	/***
	 * 支付完成 发送日志
	 * 
	 * @param expressNo
	 */
	public void sendExpressLog21004(final String expressNo) {
		// TODO Auto-generated method stub
		Thread thread = new Thread() {
			public void run() {
				JSONObject param = new JSONObject();
				param.put("createTime", DateUtils.convertToUtcTime(Calendar.getInstance().getTime()));
				param.put("type", "21004");
				param.put("orderId", expressNo);
				HttpUtil.sendWindDataLog(param);
			}
		};
		thread.start();
	}

	public JSONObject queryPrice(List<String> listExpress) {
		List<Express> list = expressService.selectByExpressNo(listExpress);
		if (list.size() == 0) {
			return JSONFactory.getErrorJSON("查找不到订单!");
		}

		Iterator<Express> iterator = list.iterator();
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal totalDownPrice = BigDecimal.ZERO;
		while (iterator.hasNext()) {
			Express next = iterator.next();
			if (App.ORDER_TYPE_AFTER.equals(next.getType()))
				continue;
			if (next.getCategory() != null) {
				totalPrice = totalPrice.add(next.getCategory().getTotalPrice());
			}
			if (next.getDownMoney() != null) {
				totalDownPrice = totalDownPrice.add(next.getDownMoney());
			}
		}

		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("totalPrice", totalPrice);
		successJSON.put("totalDownPrice", totalDownPrice);
		successJSON.put("count", list.size());
		return successJSON;
	}

	private Order save(Order order) {
		order.setCreateTime(Calendar.getInstance().getTime());
		order.setStatus(App.ORDER_CREATE);
		order.setSubStatus(App.ORDER_PRE_CREATED);
		Order save = orderRepository.save(order);
		return save;
	}

	public boolean judgeBind(String tel, String shopId) {
		return expressDao.judgeBind(tel, shopId) != null;
	}
}
