package com.mrwind.category.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mrwind.category.entity.Category;

public interface CategoryRepository extends MongoRepository<Category, Long> {
    
}
