package com.mrwind.order.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.mrwind.order.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, String> {
	
}
