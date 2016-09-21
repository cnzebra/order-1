package com.mrwind.category.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.entity.Category;
import com.mrwind.category.repositories.CategoryRepository;
import com.mrwind.common.factory.JSONFactory;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    public JSON newCategory(Category category) {
        if(category!=null) {
            category.setCreateTime(new Date(System.currentTimeMillis()));
            categoryRepository.save(category);
        }
        return JSONFactory.getSuccessJSON();
    }
}
