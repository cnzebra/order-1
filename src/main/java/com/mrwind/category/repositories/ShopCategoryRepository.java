package com.mrwind.category.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mrwind.category.entity.ShopCategory;

public interface ShopCategoryRepository extends MongoRepository<ShopCategory, String> {

    Long countByShopId(String shopId);
}
