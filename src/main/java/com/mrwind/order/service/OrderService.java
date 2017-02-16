package com.mrwind.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.cache.RedisCache;
import com.mrwind.common.factory.JSONFactory;
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

	public Order insert(Order order) {
		order.setStatus(App.ORDER_CREATE);
		order.setSubStatus(App.ORDER_PRE_CREATED);
		
		order.setUpdateTime(Calendar.getInstance().getTime());
		if(order.getDuiTimes()==null||order.getDuiTimes().size()==0){
			expressService.initExpress(order);
			order.setStatus(App.ORDER_BEGIN);
		}
		Order res = orderRepository.save(order);
		return res;
	}
	

	public void insert(List<Order> list) {
		for(Order order : list){
			insert(order);
		}
	}

	
	public void submitOrderPriced(Long orderNumber,Fence fence){
		orderDao.updateOrderStatusFence(orderNumber, App.ORDER_BEGIN,App.ORDER_BEGIN, fence);
	}

	public void completeOrder(Long orderNumber) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,App.ORDER_FINISHED);
	}
	
	public void errorCompleteOrder(Long orderNumber,String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_COMPLETE,subStatus);
	}

	public boolean cancelOrder(Long orderNumber, String subStatus) {
		orderDao.updateOrderStatus(orderNumber, App.ORDER_CANCLE,subStatus);
		return true;
	}

	public Order selectByOrder(Order order) {
		Example<Order> example=Example.of(order);
		return orderRepository.findOne(example);
	}

	public JSONObject pay(List<Long> listExpress) {
		List<Express> list = expressService.selectByExpressNo(listExpress);
		if(list.size()==0){
			return JSONFactory.getErrorJSON("查找不到订单，无法支付!");
		}
		
		List<OrderReceipt> listReceipt=new ArrayList<>();
		Iterator<Express> iterator = list.iterator();
		String tranNo = UUIDUtils.create().toString();
		BigDecimal totalPrice=BigDecimal.ZERO;
		BigDecimal totalDownPrice=BigDecimal.ZERO;
		String shopId="";
		while(iterator.hasNext()){
			Express next = iterator.next();
			if(next.getSubStatus().equals(App.ORDER_PRE_CREATED)){
				return JSONFactory.getErrorJSON("有订单未定价，无法支付，订单号为:"+next.getExpressNo()+"，订单单号为:"+next.getBindExpressNo());
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
			totalPrice=totalPrice.add(orderReceipt.getPrice());
			totalDownPrice=totalDownPrice.add(next.getDownMoney());
			orderReceipt.setTranNo(tranNo);
			listReceipt.add(orderReceipt);
		}
	
		orderReceiptRepository.save(listReceipt);

		JSONObject successJSON = JSONFactory.getSuccessJSON();
		successJSON.put("content", listReceipt);
		successJSON.put("totalPrice", totalPrice.subtract(totalDownPrice));
		successJSON.put("totalDownPrice", totalDownPrice);
		successJSON.put("tranNo", tranNo);
		successJSON.put("shopId", shopId);
		redisCache.set("transaction_"+tranNo, 3600, successJSON.toJSONString());
		return successJSON;
	}


	public JSONObject queryTranSactionDetail(String tranNo) {
		String successJSON = redisCache.getString("transaction_"+tranNo);
		if(StringUtils.isBlank(successJSON)){
			return JSONFactory.getErrorJSON("交易号已经超时，请重新发起交易！");
		}
		return JSONObject.parseObject(successJSON);
	}
}
