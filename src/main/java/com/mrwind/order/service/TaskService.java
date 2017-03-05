package com.mrwind.order.service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
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
import com.mrwind.order.repositories.OrderRepository;

@Service
public class TaskService {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

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
		String content = "尊敬的风先生用户，截止到"+nowDate+"，您还有{0}订单尚未支付，总计{1}元。我们将于21:00在您的账户余额中进行扣款，订单详情请登录风先生VIP 网页发货端进行查询。http://vip.123feng.com";
		for (Express e : specialExpresses){
			ShopAfterExpress shopAfterExpress = map.get(e);
			if(shopAfterExpress==null){
				shopAfterExpress=new ShopAfterExpress();
				shopAfterExpress.setShopId(e.getShop().getId());
				shopAfterExpress.setTotalPrice(e.getCategory().getTotalPrice());
				Set<String> experssNo=new HashSet<>();
				experssNo.add(e.getExpressNo());
				shopAfterExpress.setExperssNo(experssNo);
			}
			
			BigDecimal totalPrice = shopAfterExpress.getTotalPrice();
			shopAfterExpress.setTotalPrice(totalPrice.add(e.getCategory().getTotalPrice()));
			Set<String> experssNoSet = shopAfterExpress.getExperssNo();
			experssNoSet.add(e.getExpressNo());
			shopAfterExpress.setExperssNo(experssNoSet);
		}

		for(Entry<String, ShopAfterExpress> entry : map.entrySet()){
			String key = entry.getKey();
			ShopAfterExpress value = entry.getValue();
			HashSet<String> userIds = new HashSet<>();
			userIds.add(key);
			MessageFormat.format(content,value.getExperssNo().size(),value.getTotalPrice());
			HttpUtil.sendSMSToUserId(content, userIds);
		}
		String jsonString = JSONObject.toJSONString(map);
		redisCache.set(App.RDKEY_AFTER_ORDER, 3600*4,jsonString);
	}

	public void chargeBack(){
		JSONObject jsonObject = JSON.parseObject(redisCache.getString(App.RDKEY_AFTER_ORDER));
		
		for(Entry<String, Object> entry : jsonObject.entrySet()){
			ShopAfterExpress value =JSONObject.toJavaObject((JSONObject)entry.getValue(),ShopAfterExpress.class);
			JSONObject resJson=orderService.systemPay(value.getExperssNo(), "系统定时收款");
			if(resJson==null){
				System.out.println("系统收款失败!");
				return;
			}
			boolean balancePay = HttpUtil.balancePay(resJson.getString("tranNo"));
			String content = "您的账户余额不足，请及时充值，以免影响后续发货。";
			if(balancePay){
				content="您的账户于21：00成功支付"+resJson.getBigDecimal("totalPrice")+"元。感谢使用风先生，祝您生活愉快。";
			}
			
			Collection<String> userIds=new HashSet<>();
			userIds.add(entry.getKey());
			
			HttpUtil.sendSMSToUserId(content, userIds);
		}
	}
	
	/***
	 * 商户 后录单
	 * @author imacyf0012
	 *
	 */
	class ShopAfterExpress{
		private String shopId;
		private Set<String> experssNo;
		private BigDecimal totalPrice;
		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public Set<String> getExperssNo() {
			return experssNo;
		}
		public void setExperssNo(Set<String> experssNo) {
			this.experssNo = experssNo;
		}
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
	}

}
