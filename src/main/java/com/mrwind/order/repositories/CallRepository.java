package com.mrwind.order.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mrwind.order.entity.Call;

public interface CallRepository extends MongoRepository<Call,Long>{

}
