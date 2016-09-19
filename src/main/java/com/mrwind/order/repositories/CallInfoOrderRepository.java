package com.mrwind.order.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mrwind.order.entity.User;

public interface CallInfoOrderRepository extends MongoRepository<User,Long> {

}
