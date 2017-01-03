package com.mrwind.category.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.mrwind.category.entity.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
 
    Long countByName(String name);
    
    Long countByNameAndCategory(String name, String category);
    
    @Query(value="{'name' : ?0, 'distance.a' : ?1, 'distance.b' : ?2}", fields="{'name' : 1}")
    List<String> findTheDistanceByNameAndDistance(String name, Double distanceA, Double distanceB);
    
}
