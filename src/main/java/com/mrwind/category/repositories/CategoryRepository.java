package com.mrwind.category.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mrwind.category.entity.Category;

public interface CategoryRepository extends MongoRepository<Category, Long> {
 
    Long countByName(String name);
    
    Long countByNameAndCategory(String name, String category);
    
}
