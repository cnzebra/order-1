package com.mrwind.category.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.category.entity.CategoryAddition;
import com.mrwind.category.repositories.CategoryAdditionRepository;
import com.mrwind.common.factory.JSONFactory;

@Service
public class CategoryAdditionService {

    @Autowired
    CategoryAdditionRepository categoryAdditionRepository;
    
    public JSON save(CategoryAddition categoryAddition) {
        if(categoryAddition==null || StringUtils.isBlank(categoryAddition.getName())) {
            return JSONFactory.getfailJSON("请输入特殊要求");
        }
        Long count = categoryAdditionRepository.countByName(categoryAddition.getName());
        if(count!=null && count>0) {
            return JSONFactory.getfailJSON("当前特殊要求已经存在");
        }
        categoryAdditionRepository.save(categoryAddition);
        return JSONFactory.getSuccessJSON();
    }
    
    public JSON getAllNotDel() {
        List<CategoryAddition> data = categoryAdditionRepository.findByDelFlag(false);
        if(data!=null && data.size()>0) {
            JSONObject jsonObject = JSONFactory.getSuccessJSON();
            jsonObject.put("data", data);
            return jsonObject;
        } else {
            JSONObject jsonObject = JSONFactory.getSuccessJSON();
            jsonObject.put("data", "");
            return jsonObject;
        }
    }
}
