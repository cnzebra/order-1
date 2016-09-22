package com.mrwind.category.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.dao.CategoryDao;
import com.mrwind.category.entity.Category;
import com.mrwind.category.repositories.CategoryRepository;
import com.mrwind.common.factory.JSONFactory;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryDao categoryDao;
    
    public JSON newCategory(Category category) {
        if(category==null) {
            return JSONFactory.getfailJSON("参数错误");
        }
        Long count = categoryDao.countByNameAndDistance(
                        category.getName(), 
                        category.getDistance().getA(),
                        category.getDistance().getB());
        if(count!=null && count>0) {
            return JSONFactory.getfailJSON("当前品类模板已经存在");
        }
        if(category!=null) {
            category.setCreateTime(new Date(System.currentTimeMillis()));
            categoryRepository.save(category);
        }
        return JSONFactory.getSuccessJSON();
    }
    
    public Long countByModel(String name, Double fromDistance, Double toDistance) {
        return categoryDao.countByNameAndDistance(name, fromDistance, toDistance);
    }
}
