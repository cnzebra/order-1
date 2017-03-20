package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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


	public void sendWarning(){
		List<Express> specialExpresses = expressDao.findByTypeAndStatusAndSubStatus(App.ORDER_TYPE_AFTER, App.ORDER_COMPLETE, App.ORDER_PRE_PRICED);
		Map<String,ShopAfterExpress> map = new HashMap<>();

		String nowDate = DateUtils.getDate("yyyy年MM月dd日 HH:mm");
		Integer count;
		BigDecimal price;

		for (Express e : specialExpresses){
			ShopAfterExpress shopAfterExpress = map.get(e.getShop().getId());
			if(shopAfterExpress==null){
				shopAfterExpress=new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo=new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				map.put(e.getShop().getId(),shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for(Entry<String, ShopAfterExpress> entry : map.entrySet()){
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			//MessageFormat.format(content,value.getExpressNo().size(),value.getTotalPrice());
			count = value.getExpressNo().size();
			price = value.getTotalPrice();
			HttpUtil.sendSMSToUserId(getContent(nowDate,count,price), userIds);
		}
		String jsonString = JSONObject.toJSONString(map);
		redisCache.set(App.RDKEY_AFTER_ORDER, 3600*4,jsonString);
		System.out.println("sendWarning");
	}

	public void chargeBack(){
		JSONObject jsonObject = JSON.parseObject(redisCache.getString(App.RDKEY_AFTER_ORDER));

		for(Entry<String, Object> entry : jsonObject.entrySet()){
			JSONObject jsonObject1 = (JSONObject)entry.getValue();
			ShopAfterExpress value =JSONObject.toJavaObject(jsonObject1,ShopAfterExpress.class);
			JSONObject resJson=orderService.systemPay(value.getExpressNo(), "系统定时收款");
			if(resJson==null){
				System.out.println("系统收款失败!");
				continue;
			}
			boolean balancePay = HttpUtil.balancePay(resJson.getString("tranNo"));
			String content = "您的账户余额不足，请及时充值，以免影响后续发货。";
			if(balancePay){
				content="您的账户于21：00成功支付"+resJson.getBigDecimal("totalPrice")+"元。感谢使用风先生，祝您生活愉快。";
				Set<String> set = value.getExpressNo();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String expressNo = it.next();
					expressDao.updateStatus(expressNo,App.ORDER_COMPLETE,App.ORDER_COMPLETE);
				}

			}

			Collection<String> userIds=new HashSet<>();
			userIds.add(entry.getKey());

			HttpUtil.sendSMSToUserId(content, userIds);
			System.out.println("chargeBack");
		}
		
		//处理预付款
		creditPay();
		
		
	}

	private void creditPay() {
		// TODO Auto-generated method stub
		List<Express> findBySubStatus = expressRepository.findBySubStatus(App.ORDER_PRE_PAY_CREDIT);
		
		Map<String,ShopAfterExpress> map = new HashMap<>();
		
		for (Express e : findBySubStatus){
			ShopAfterExpress shopAfterExpress = map.get(e.getShop().getId());
			if(shopAfterExpress==null){
				shopAfterExpress=new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> expressNo=new HashSet<>();
				expressNo.add(e.getExpressNo());
				shopAfterExpress.setExpressNo(expressNo);
				map.put(e.getShop().getId(),shopAfterExpress);
				continue;
			}

			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> expressNoSet = shopAfterExpress.getExpressNo();
			expressNoSet.add(e.getExpressNo());
		}

		for(Entry<String, ShopAfterExpress> entry : map.entrySet()){
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			JSONObject resJson=orderService.systemPay(value.getExpressNo(), "系统定时收款");
			if(resJson==null){
				System.out.println("系统收款失败!");
				continue;
			}
			boolean balancePay = HttpUtil.balancePay(resJson.getString("tranNo"));
			String content = "本次扣款失败，您的余额不足，请及时联系风先生充值。";
			if(balancePay){
				content="您的账户于21：00成功支付"+resJson.getBigDecimal("totalPrice")+"元。感谢使用风先生，祝您生活愉快。";
				Set<String> set = value.getExpressNo();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					String expressNo = it.next();
					expressDao.updateSubStatus(expressNo, App.ORDER_PRE_PAY_PRICED);
				}
			}
			HttpUtil.sendSMSToUserId(content, userIds);
		}
	}


	private String getContent(String date,Integer count,BigDecimal price){
		return "尊敬的风先生用户，截止到"+date+"，您还有"+ count +"订单尚未支付，总计"+ price +"元。我们将于21:00在您的账户余额中进行扣款，订单详情请登录风先生VIP 网页发货端进行查询。http://vip.123feng.com";
	}
	/***
	 * 商户 后录单
	 * @author imacyf0012
	 *
	 */
/*	class ShopAfterExpress{
		private String shopId;
		private Set<String> expressNo;
		private BigDecimal totalPrice;
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public Set<String> getExpressNo() {
			return expressNo;
		}
		public void setExpressNo(Set<String> expressNo) {
			this.expressNo = expressNo;
		}
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
	}*/

}
