package com.mrwind.category.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mrwind.category.entity.CategoryAddition;

public interface CategoryAdditionRepository extends MongoRepository<CategoryAddition, Long>{
    
    List<CategoryAddition> findByDelFlag(boolean del_flag);
    
    Long countByName(String name);
}
