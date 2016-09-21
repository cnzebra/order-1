package com.mrwind.category.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mrwind.category.entity.CategoryType;

public interface TypeRepository extends MongoRepository<CategoryType, Long> {
    
    Long countByName(String name);
}
