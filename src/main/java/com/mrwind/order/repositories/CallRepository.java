package com.mrwind.order.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mrwind.order.entity.Call;

public interface CallRepository extends PagingAndSortingRepository<Call,String>{
	
}
