package com.mrwind.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mrwind.order.entity.ShopSender;
import com.mrwind.order.repositories.ShopSenderRepository;

@Service
public class UserService {
	
	@Autowired
	private ShopSenderRepository shopSenderRepository;

	public List<ShopSender> queryShopSenderInfo(String shopId, PageRequest page) {
		
		List<ShopSender> res= shopSenderRepository.findByShopInfoId(shopId,page);
		return res;
	}
	
	public ShopSender queryShopSenderInfo(String pkId){
		return shopSenderRepository.findOne(pkId);
	}
	
	public ShopSender save(ShopSender shopSender){
		ShopSender res = shopSenderRepository.save(shopSender);
		return res;
	}
	
	public void delete(String shopSenderId){
		shopSenderRepository.delete(shopSenderId);
	}
	
	public void update( ){
	}
}
