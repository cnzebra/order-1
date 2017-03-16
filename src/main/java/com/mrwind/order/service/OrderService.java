package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import com.mrwind.common.util.UUIDUtils;
import com.mrwind.order.App;
import com.mrwind.order.dao.OrderDao;
import com.mrwind.order.entity.Express;
import com.mrwind.order.entity.Fence;
import com.mrwind.order.entity.Order;
import com.mrwind.order.entity.OrderReceipt;
import com.mrwind.order.repositories.OrderReceiptRepository;
import com.mrwind.order.repositories.OrderRepository;

import static com.mrwind.common.constant.ConfigConstant.API_WECHAT_HOST;

@Service
public class OrderService {

	static Logger log = Logger.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired ExpressService expressService;
	
	@Autowired OrderReceiptRepository orderReceiptRepository;
	
	@Autowired RedisCache redisCache;
	
	@Autowired
	private OrderDao orderDao;

	public List<Express> initAndInsert(Order order) {
		Order resOrder = save(order);
		return expressService.createExpress(resOrder);
	}

	public List<Express> initAndInsert(List<Order> list) {
		List<Order> newList=new ArrayList<>();
		for(Order order : list){
			Order save = save(order);
			newList.add(save);
		}
		System.out.println(System.currentTimeMillis());
		return expressService.createExpress(newList);
	}

	public Order selectByOrder(Order order) {
		Example<Order> example=Example.of(order);
		return orderRepository.findOne(example);
	}
	
	public Page<Order> selectAllByOrder(Order order, Integer pageIndex, Integer pageSize) {
		Sort sort = new Sort(Direction.DESC,"createTime");
		PageRequest page =new PageRequest(pageIndex, pageSize,sort);

		Example<Order> example=Example.of(order);
		return orderRepository.findAll(example,page);
	}

	public synchronized JSONObject pay(List<String> listExpress, String userId) {
		List<Express> list = expressService.selectByExpressNo(listExpress);
		if(list.size()==0){
			return JSONFactory.getErrorJSON("查找不到订单，无法支付!");
		}
		
		
		List<OrderReceipt> listReceipt=new ArrayList<>();
		Iterator<Express> iterator = list.iterator();
		String tranNo = UUIDUtils.getUUID();
		BigDecimal totalPrice=BigDecimal.ZERO;
		BigDecimal totalDownPrice=BigDecimal.ZERO;
		BigDecimal totalServicePrice=BigDecimal.ZERO;
		String shopId="";
		int i=0;
		JSONArray jsonArray=new JSONArray();
		while(iterator.hasNext()){
			Express next = iterator.next();
			if(App.ORDER_TYPE_AFTER.equals(next.getType())){
//				return JSONFactory.getErrorJSON("订单"+(next.getBindExpressNo()==null?next.getExpressNo():next.getBindExpressNo())+"为后录单，请先处理后再发起罚款!");
//				JSONObject tmp=new JSONObject();
//				tmp.put("order", next.getExpressNo());
//				tmp.put("status", "COMPLETE");
//				tmp.put("orderType", "A");
//				tmp.put("sendLog", false);
//				tmp.put("des", "收件完成");
//				jsonArray.add(tmp);
				continue;
			}
			if(next.getSubStatus().equals(App.ORDER_PRE_CREATED)){
				return JSONFactory.getErrorJSON("未定价，订单号:"+next.getExpressNo()+(next.getBindExpressNo()==null?"。":("，绑定单号为:"+next.getBindExpressNo())));
			}
			if(!next.getStatus().equals(App.ORDER_BEGIN)){
				return JSONFactory.getErrorJSON("订单当前状态无法支付，订单号为:"+next.getExpressNo()+(next.getBindExpressNo()==null?"。":("，绑定单号为:"+next.getBindExpressNo())));
			}
//			if(redisCache.hget(App.RDKEY_PAY_ORDER, next.getExpressNo().toString())!=null){   改为更新交易号的方式
//				return JSONFactory.getErrorJSON("订单正在支付，无法重复发起支付!");
//			}
			if(next.getShop()!=null){
				if(shopId.equals("")){
					shopId= next.getShop().getId();
				}
				if(!shopId.equals(next.getShop().getId())){
					return JSONFactory.getErrorJSON("发送的订单数据异常，不属于同一个商户，无法支付");
				}
			}
			OrderReceipt orderReceipt = new OrderReceipt(next);
			if(i!=0){
				totalServicePrice.add(orderReceipt.getCategory().getServicePrice());
			}else{
				i++;
			}
			totalPrice=totalPrice.add(orderReceipt.getPrice());
			if(next.getDownMoney()!=null){
				totalDownPrice=totalDownPrice.add(next.getDownMoney());
			}
			if(jsonArray.size()>0){
				HttpUtil.compileExpressMission(jsonArray);
			}
			
			Object key = redisCache.hget(App.RDKEY_PAY_ORDER, next.getExpressNo().toString());
			if(key!=null){
				String strKey = key.toString();
				redisCache.hdel(App.RDKEY_PAY_ORDER.getBytes(), next.getExpressNo().getBytes());
				redisCache.delete("transaction_"+strKey);
			}
			redisCache.hset(App.RDKEY_PAY_ORDER,next.getExpressNo(),tranNo);
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
		if(StringUtils.isNoneEmpty(userId)){
			successJSON.put("executor", HttpUtil.findPrivateUserInfo(userId));
		}

		redisCache.set("transaction_"+tranNo, 3600, successJSON.toJSONString());
		return successJSON;
	}

	/***
	 * 系统发起付款
	 * @param expressNo
	 * @param string
	 * @return
	 */
	public JSONObject systemPay(Set<String> expressNo, String string) {
		// TODO Auto-generated method stub
		List<Express> list = expressService.selectByExpressNo(expressNo);
		if(list.size()==0){
			return null;
		}

		List<OrderReceipt> listReceipt=new ArrayList<>();
		Iterator<Express> iterator = list.iterator();
		String tranNo = UUIDUtils.getUUID();
		BigDecimal totalPrice=BigDecimal.ZERO;
		BigDecimal totalDownPrice=BigDecimal.ZERO;
		String shopId="";
		int i=0;
		while(iterator.hasNext()){
			Express next = iterator.next();
			if(!App.ORDER_TYPE_AFTER.equals(next.getType())){
				return null;
			}
			
			if(next.getShop()!=null){
				if(shopId.equals("")){
					shopId= next.getShop().getId();
				}
				if(!shopId.equals(next.getShop().getId())){
					return null;
				}
			}
			
			OrderReceipt orderReceipt = new OrderReceipt(next);
			totalPrice=totalPrice.add(orderReceipt.getPrice());
			if(next.getDownMoney()!=null){
				totalDownPrice=totalDownPrice.add(next.getDownMoney());
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

		redisCache.set("transaction_"+tranNo, 3600, successJSON.toJSONString());
		return successJSON;
	}

	public JSONObject queryTranSactionDetail(String tranNo) {
		String successJSON = redisCache.getString("transaction_"+tranNo);
		if(StringUtils.isBlank(successJSON)){
			return JSONFactory.getErrorJSON("交易已经关闭!");
		}
		return JSONObject.parseObject(successJSON);
	}


	public JSONObject lockOrder(String tranNo) {
		String res = redisCache.getString("transaction_"+tranNo);
		if(StringUtils.isEmpty(res)){
			return JSONFactory.getErrorJSON("不用锁单了，请求号已经无效");
		}
		JSONObject jsonObject = JSONObject.parseObject(res);
		JSONArray items = jsonObject.getJSONArray("content");
		Iterator<Object> iterator = items.iterator();
		while(iterator.hasNext()){
			OrderReceipt next =JSONObject.toJavaObject((JSONObject) iterator.next(),OrderReceipt.class);
			redisCache.hset(App.RDKEY_PAY_ORDER, next.getExpressNo().toString(), 1);
		}
		return JSONFactory.getSuccessJSON();
	}

	public String payCallback(String tranNo) {
		List<OrderReceipt> list=orderReceiptRepository.findAllByTranNo(tranNo);
		redisCache.delete("transaction_"+tranNo);
		log.info("付款回调:" + tranNo);
		JSONArray json=new JSONArray();
		StringBuffer sb=new StringBuffer();
		for (OrderReceipt orderReceipt : list){
			expressService.udpateExpressStatus(orderReceipt.getExpressNo(),App.ORDER_SENDING,App.ORDER_PRE_PAY_PRICED);
			JSONObject tmp=new JSONObject();
			tmp.put("order", orderReceipt.getExpressNo());
			tmp.put("status", "COMPLETE");
			tmp.put("orderType", "R");
			tmp.put("sendLog", true);
			tmp.put("des", "支付完成");
			json.add(tmp);
			sb.append(orderReceipt.getExpressNo()+",");
			redisCache.hdel(App.RDKEY_PAY_ORDER.getBytes(), orderReceipt.getExpressNo().toString().getBytes());
			expressService.updateLineIndex(orderReceipt.getExpressNo(),1);
			//发送短信
			if (orderReceipt.getSender() != null && orderReceipt.getReceiver()!= null) {
				String expressNo = StringUtils.isNotBlank(orderReceipt.getBindExpressNo()) ? orderReceipt.getBindExpressNo() : orderReceipt.getExpressNo();
				String content = "尊敬的客户您好，"+orderReceipt.getSender().getName()+"寄给您的快件已由风先生配送，单号:" + expressNo + "，点此链接跟踪运单："+API_WECHAT_HOST+"#/phone/orderTrace/"+expressNo;
				HttpUtil.sendSMSToUserTel(content, orderReceipt.getReceiver().getTel());
			}
		}
		if(sb.length()>0){
			String express = sb.substring(0, sb.length()-1);
			sendExpressLog21004(express);
		}
		log.info("需要完成的单号 : " + json.toJSONString());
		log.info("完成任务是否成功 :" + HttpUtil.compileExpressMission(json));
	
		return null;
	}
	
	/***
	 * 支付完成 发送日志
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
		if(list.size()==0){
			return JSONFactory.getErrorJSON("查找不到订单!");
		}
		
		Iterator<Express> iterator = list.iterator();
		BigDecimal totalPrice=BigDecimal.ZERO;
		BigDecimal totalDownPrice=BigDecimal.ZERO;
		while(iterator.hasNext()){
			Express next = iterator.next();
			if(App.ORDER_TYPE_AFTER.equals(next.getType()))continue;
			if(next.getCategory()!=null){
				totalPrice=totalPrice.add(next.getCategory().getTotalPrice());
			}
			if(next.getDownMoney()!=null){
				totalDownPrice=totalDownPrice.add(next.getDownMoney());
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
	
	@Deprecated
	public void submitOrderPriced(Long orderNumber,Fence fence){
		orderDao.updateOrderStatusFence(orderNumber, App.ORDER_BEGIN,App.ORDER_BEGIN, fence);
	}

	@Deprecated
	public void completeOrder(Long orderNumber) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,App.ORDER_COMPLETE);
	}
	
	@Deprecated
	public void errorCompleteOrder(Long orderNumber,String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,subStatus);
	}

	@Deprecated
	public boolean cancelOrder(Long orderNumber, String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_CANCEL,subStatus);
		return true;
	}
}
