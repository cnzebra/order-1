package com.mrwind.order.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mrwind.order.entity.ShopSender;

public interface ShopSenderRepository extends PagingAndSortingRepository<ShopSender, String> {
	List<ShopSender> findByShopInfoId(String shopId, PageRequest page);
	
}
