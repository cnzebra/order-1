package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.util.DateUtils;
import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.App;
import com.mrwind.order.dao.ExpressDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.ShopAfterExpress;
import com.mrwind.order.repositories.ExpressRepository;
import com.mrwind.order.repositories.OrderRepository;

@Service
public class TaskService {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ExpressRepository expressRepository;

	@Autowired
	ExpressDao expressDao;

	@Autowired
	ExpressService expressService;

	@Autowired
	RedisCache redisCache;

	public void sendOrder() {

		if (redisCache.getObject(this.getClass().getName() + "sendOrder") != null) {
			return;
		}
		redisCache.set(this.getClass().getName() + "sendOrder", 20, true);

		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.HOUR_OF_DAY, -2);

		List<Express> list = expressDao.findUnBegin(instance.getTime());
		if (list == null || list.size() == 0)
			return;

		Map<String, List<Express>> map = new HashMap<>();
		for (Express express : list) {
			expressDao.updateStatus(express.getExpressNo(), App.ORDER_BEGIN, App.ORDER_PRE_CREATED);

			List<Express> listTmp = map.get(express.getExpressNo());
			if (listTmp == null) {
				listTmp = new ArrayList<>();
			}
			listTmp.add(express);
			map.put(express.getExpressNo(), listTmp);
		}

		for (Map.Entry<String, List<Express>> entry : map.entrySet()) {
			expressService.sendExpressLog21010(entry.getValue());
		}
	}

	/**
	 * 生成账单 信用扣款
	 */
	public void generateBill() {

		if (redisCache.getObject(this.getClass().getName() + "generateBill") != null) {
			return;
		}
		redisCache.set(this.getClass().getName() + "generateBill", 360, true);

		Map<String, BigDecimal> totalPriceMap = new HashMap<>();

		Map<String, ShopAfterExpress> afterMap = new HashMap<>();
		List<Express> afterPayList = expressDao.findByTypeAndStatusAndSubStatus(App.ORDER_TYPE_AFTER,
				App.ORDER_COMPLETE, App.ORDER_PRE_PRICED);

		// 遍历获取到shopId对应的订单号
		List<Express> creditPayList = expressRepository.findBySubStatus(App.ORDER_PRE_PAY_CREDIT);
		Map<String, ShopAfterExpress> creditMap = new HashMap<>();
		for (Express e : creditPayList) {

			ShopAfterExpress shopAfterExpress = creditMap.get(e.getShop().getId());
			if (shopAfterExpress == null) {
				shopAfterExpress = new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo = new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				creditMap.put(e.getShop().getId(), shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for (Express e : afterPayList) {
			ShopAfterExpress shopAfterExpress = afterMap.get(e.getShop().getId());

			if (shopAfterExpress == null) {
				shopAfterExpress = new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo = new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				afterMap.put(e.getShop().getId(), shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for (Entry<String, ShopAfterExpress> entry : creditMap.entrySet()) {
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			Set<String> userIds = new HashSet<>();
			userIds.add(key);
			JSONObject resJson = orderService.systemPay(value.getExpressNo(), "系统定时收款");
			if (resJson == null) {
				System.out.println("系统收款失败!");
				continue;
			}

			boolean balancePay = HttpUtil.creditPay(resJson.getString("tranNo"));
			if (balancePay) {
				// 保存总共的消费额
				BigDecimal totalPrice = resJson.getBigDecimal("totalPrice");
				BigDecimal money = totalPriceMap.get(key);
				if (money == null) {
					totalPriceMap.put(key, totalPrice);
				} else {
					totalPriceMap.put(key, money.add(totalPrice));
				}

				Set<String> set = value.getExpressNo();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String expressNo = it.next();
					expressDao.updateSubStatus(expressNo, App.ORDER_PRE_PAY_PRICED);
				}
			}
		}

		for (Entry<String, ShopAfterExpress> entry : afterMap.entrySet()) {
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			JSONObject resJson = orderService.systemPay(value.getExpressNo(), "系统定时收款");
			if (resJson == null) {
				System.out.println("系统收款失败!");
				continue;
			}

			boolean balancePay = HttpUtil.creditPay(resJson.getString("tranNo"));
			if (balancePay) {
				// 保存总共的消费额
				BigDecimal totalPrice = resJson.getBigDecimal("totalPrice");
				BigDecimal money = totalPriceMap.get(key);
				if (money == null) {
					totalPriceMap.put(key, totalPrice);
				} else {
					totalPriceMap.put(key, money.add(totalPrice));
				}
				// content = "您的账户于21：00成功支付" +
				// resJson.getBigDecimal("totalPrice") + "元。感谢使用风先生，祝您生活愉快。";
				Set<String> set = value.getExpressNo();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String expressNo = it.next();
					expressDao.updateStatus(expressNo, App.ORDER_COMPLETE, App.ORDER_COMPLETE);
				}
			}
		}

		// 保存钱
		String jsonString = JSONObject.toJSONString(totalPriceMap);
		redisCache.set(App.RDKEY_SHOP_TOTAL_PRICE, 3600 * 12, jsonString);
		System.out.println("generateBill");

	}

	public void sendBill() {

		if (redisCache.getObject(this.getClass().getName() + "sendBill") != null) {
			return;
		}
		redisCache.set(this.getClass().getName() + "sendBill", 360, true);

		JSONObject jsonObject = JSON.parseObject(redisCache.getString(App.RDKEY_SHOP_TOTAL_PRICE));
		if(jsonObject == null){
			return;
		}
		Map<String, BigDecimal> balanceAmountMap = HttpUtil.getShopBalanceAmount(jsonObject.keySet());

		if (jsonObject != null && balanceAmountMap != null) {
			for (Entry<String, Object> entry : jsonObject.entrySet()) {
				BigDecimal totalPrice = BigDecimal.valueOf((Double) entry.getValue());
				String date = DateUtils.formatDate(DateUtils.addDays(new Date(), -1), "MM月dd日");
				Collection<String> userIds = new HashSet<>();
				userIds.add(entry.getKey());
				String content = "";
				if (balanceAmountMap.containsKey(entry.getKey())) {
					BigDecimal amount = balanceAmountMap.get(entry.getKey());
					if (amount.compareTo(BigDecimal.ZERO) != -1) {
						content = "你的账户" + date + "运力消费金额为" + totalPrice + "元，账户余额为" + amount
								+ "元。请点击链接立即查看消费明细或充值https://ddd.html";

					} else {
						content = "你的账户" + date + "运力消费金额为" + totalPrice + "元，实际账户已欠费" + amount
								+ "元，请尽快充值，以免影响您的信用。请点击链接立即查看消费明细或充值https://ddd.html";
					}
				} else {
					// 没有该商户的余额信息
					content = "你的账户" + date + "运力消费金额为" + totalPrice
							+ "元，请尽快充值，以免影响您的信用。请点击链接立即查看消费明细或充值https://ddd.html";
				}
				HttpUtil.sendSMSToShopId(content, userIds);
			}
		}

	}

	@Deprecated
	public void sendWarning() {

		if (redisCache.getObject(this.getClass().getName() + "sendWarning") != null) {
			return;
		}
		redisCache.set(this.getClass().getName() + "sendWarning", 3600, true);

		List<Express> specialExpresses = expressDao.findByTypeAndStatusAndSubStatus(App.ORDER_TYPE_AFTER,
				App.ORDER_COMPLETE, App.ORDER_PRE_PRICED);
		Map<String, ShopAfterExpress> map = new HashMap<>();

		String nowDate = DateUtils.getDate("yyyy年MM月dd日 HH:mm");
		Integer count;
		BigDecimal price;

		for (Express e : specialExpresses) {
			ShopAfterExpress shopAfterExpress = map.get(e.getShop().getId());
			if (shopAfterExpress == null) {
				shopAfterExpress = new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo = new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				map.put(e.getShop().getId(), shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for (Entry<String, ShopAfterExpress> entry : map.entrySet()) {
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			// MessageFormat.format(content,value.getExpressNo().size(),value.getTotalPrice());
			count = value.getExpressNo().size();
			price = value.getTotalPrice();
			HttpUtil.sendSMSToShopId(getContent(nowDate, count, price), userIds);
		}
		String jsonString = JSONObject.toJSONString(map);
		redisCache.set(App.RDKEY_AFTER_ORDER, 3600 * 4, jsonString);
		System.out.println("sendWarning");
	}

	@Deprecated
	public void chargeBack() {

		if (redisCache.getObject(this.getClass().getName() + "chargeBack") != null) {
			return;
		}
		redisCache.set(this.getClass().getName() + "chargeBack", 3600, true);

		JSONObject jsonObject = JSON.parseObject(redisCache.getString(App.RDKEY_AFTER_ORDER));
		if (jsonObject != null) {
			for (Entry<String, Object> entry : jsonObject.entrySet()) {
				JSONObject jsonObject1 = (JSONObject) entry.getValue();
				ShopAfterExpress value = JSONObject.toJavaObject(jsonObject1, ShopAfterExpress.class);
				JSONObject resJson = orderService.systemPay(value.getExpressNo(), "系统定时收款");
				if (resJson == null) {
					System.out.println("系统收款失败!");
					continue;
				}
				boolean balancePay = HttpUtil.balancePay(resJson.getString("tranNo"));
				String content = "您的账户余额不足，请及时充值，以免影响后续发货。";
				if (balancePay) {
					content = "您的账户于21：00成功支付" + resJson.getBigDecimal("totalPrice") + "元。感谢使用风先生，祝您生活愉快。";
					Set<String> set = value.getExpressNo();
					Iterator<String> it = set.iterator();
					while (it.hasNext()) {
						String expressNo = it.next();
						expressDao.updateStatus(expressNo, App.ORDER_COMPLETE, App.ORDER_COMPLETE);
					}

				}

				Collection<String> userIds = new HashSet<>();
				userIds.add(entry.getKey());

				HttpUtil.sendSMSToShopId(content, userIds);
				System.out.println("chargeBack");
			}
		}

		// 处理预付款
		creditPay();

	}

	@Deprecated
	private void creditPay() {
		// TODO Auto-generated method stub
		List<Express> findBySubStatus = expressRepository.findBySubStatus(App.ORDER_PRE_PAY_CREDIT);

		Map<String, ShopAfterExpress> map = new HashMap<>();

		for (Express e : findBySubStatus) {
			ShopAfterExpress shopAfterExpress = map.get(e.getShop().getId());
			if (shopAfterExpress == null) {
				shopAfterExpress = new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo = new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				map.put(e.getShop().getId(), shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for (Entry<String, ShopAfterExpress> entry : map.entrySet()) {
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			JSONObject resJson = orderService.systemPay(value.getExpressNo(), "系统定时收款");
			if (resJson == null) {
				System.out.println("系统收款失败!");
				continue;
			}
			boolean balancePay = HttpUtil.balancePay(resJson.getString("tranNo"));
			String content = "本次扣款失败，您的余额不足，请及时联系风先生充值。";
			if (balancePay) {
				content = "您的账户于21：00成功支付" + resJson.getBigDecimal("totalPrice") + "元。感谢使用风先生，祝您生活愉快。";
				Set<String> set = value.getExpressNo();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String expressNo = it.next();
					expressDao.updateSubStatus(expressNo, App.ORDER_PRE_PAY_PRICED);
				}
			}
			HttpUtil.sendSMSToShopId(content, userIds);
		}
	}

	private String getContent(String date, Integer count, BigDecimal price) {
		return "尊敬的风先生用户，截止到" + date + "，您还有" + count + "订单尚未支付，总计" + price
				+ "元。我们将于21:00在您的账户余额中进行扣款，订单详情请登录风先生VIP 网页发货端进行查询。http://vip.123feng.com";
	}
	/***
	 * 商户 后录单
	 *
	 * @author imacyf0012
	 *
	 */
	/*
	 * class ShopAfterExpress{ private String shopId; private Set<String>
	 * expressNo; private BigDecimal totalPrice; public String getShopId() {
	 * return shopId; } public void setShopId(String shopId) { this.shopId =
	 * shopId; } public Set<String> getExpressNo() { return expressNo; } public
	 * void setExpressNo(Set<String> expressNo) { this.expressNo = expressNo; }
	 * public BigDecimal getTotalPrice() { return totalPrice; } public void
	 * setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; } }
	 */

}
