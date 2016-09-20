package com.mrwind.order.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mrwind.order.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
	
}
