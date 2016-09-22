package com.mrwind.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.entity.CategoryType;
import com.mrwind.category.repositories.TypeRepository;
import com.mrwind.common.factory.JSONFactory;

@Service
public class TypeService {

    @Autowired
    private TypeRepository typeRepository;
    
    public JSON newCategoryType(String name) {
        Long count = typeRepository.countByName(name);
        if(count!=null && count>0) {
            return JSONFactory.getfailJSON("当前品类类型已经存在");
        }
        CategoryType categoryType = new CategoryType();
        categoryType.setName(name);
        typeRepository.save(categoryType);
        
        return JSONFactory.getSuccessJSON();
    }
}
