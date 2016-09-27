package com.mrwind.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mrwind.order.entity.ShopReceiver;
import com.mrwind.order.entity.ShopSender;
import com.mrwind.order.repositories.ShopReceiverRepository;
import com.mrwind.order.repositories.ShopSenderRepository;

@Service
public class UserService {
	
	@Autowired
	private ShopSenderRepository shopSenderRepository;
	
	@Autowired
	private ShopReceiverRepository shopReceiverRepository;

	public List<ShopSender> queryShopSenderInfo(String shopId, PageRequest page) {
		
		List<ShopSender> res= shopSenderRepository.findByShopInfoId(shopId,page);
		return res;
	}
	
	public ShopSender queryShopSenderInfo(String pkId){
		return shopSenderRepository.findOne(pkId);
	}
	
	public ShopSender saveSender(ShopSender shopSender){
		ShopSender res = shopSenderRepository.save(shopSender);
		return res;
	}
	
	
	public void delete(String shopSenderId){
		shopSenderRepository.delete(shopSenderId);
	}
	
	public ShopReceiver queryShopReceiverInfo(String pkId){
		return shopReceiverRepository.findOne(pkId);
	}
	
	public ShopReceiver saveReceiver(ShopReceiver shopReceiver){
		ShopReceiver res=shopReceiverRepository.save(shopReceiver);
		return res;
	}

	public List<ShopReceiver> queryShopReceiverInfo(String shopId, PageRequest page) {
		List<ShopReceiver> res= shopReceiverRepository.findByShopInfoId(shopId,page);
		return res;
	}
}
