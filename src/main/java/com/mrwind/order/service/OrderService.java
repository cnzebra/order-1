package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired ExpressService expressService;
	
	@Autowired OrderReceiptRepository orderReceiptRepository;
	
	@Autowired RedisCache redisCache;
	
	@Autowired
	private OrderDao orderDao;

	public List<Express> initAndInsert(Order order) {
		order.setStatus(App.ORDER_CREATE);
		order.setSubStatus(App.ORDER_PRE_CREATED);
		order.setUpdateTime(Calendar.getInstance().getTime());
		orderRepository.save(order);
		List<Express> list = expressService.createExpress(order);
		return list;
	}
	

	public void initAndInsert(List<Order> list) {
		for(Order order : list){
			initAndInsert(order);
		}
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
		orderDao.updateOrderStatus(orderNumber, App.ORDER_CANCLE,subStatus);
		return true;
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
		while(iterator.hasNext()){
			Express next = iterator.next();
			if(next.getSubStatus().equals(App.ORDER_PRE_CREATED)){
				return JSONFactory.getErrorJSON("有订单未定价，无法支付，订单号为:"+next.getExpressNo()+(next.getBindExpressNo()==null?"。":("，绑定单号为:"+next.getBindExpressNo())));
			}
			if(!next.getStatus().equals(App.ORDER_BEGIN)){
				return JSONFactory.getErrorJSON("订单当前状态无法支付，订单号为:"+next.getExpressNo()+(next.getBindExpressNo()==null?"。":("，绑定单号为:"+next.getBindExpressNo())));
			}
			if(redisCache.hget(App.RDKEY_PAY_ORDER, next.getExpressNo().toString())!=null){
				return JSONFactory.getErrorJSON("订单正在支付，无法重复发起支付!");
			}
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
		}
		if(sb.length()>0){
			String express = sb.substring(0, sb.length()-1);
			sendExpressLog21004(express);
		}
		HttpUtil.compileExpressMission(json);
	
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
}
