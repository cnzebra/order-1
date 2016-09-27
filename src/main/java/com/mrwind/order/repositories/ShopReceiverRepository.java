package com.mrwind.order.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mrwind.order.entity.ShopReceiver;

public interface ShopReceiverRepository extends PagingAndSortingRepository<ShopReceiver, String> {
	List<ShopReceiver> findByShopInfoId(String shopId, PageRequest page);
	
}
