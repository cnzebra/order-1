package com.mrwind.category.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.category.dao.CategoryDao;
import com.mrwind.category.entity.Category;
import com.mrwind.category.entity.CategoryAddition;
import com.mrwind.category.entity.Distance;
import com.mrwind.category.entity.RaisePrice;
import com.mrwind.category.repositories.CategoryRepository;
import com.mrwind.common.bean.SpringDataPageable;
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
    
    public JSON findAllByPage(Integer pageno, Integer pagesize) {
        SpringDataPageable pageable = new SpringDataPageable();
        if(pageno!=null) {
            pageable.setPagenumber(pageno);
        }
        if(pagesize!=null) {
            pageable.setPagesize(pagesize);
        }
        List<Order> orders = new ArrayList<Order>();  //排序
        orders.add(new Order(Direction.ASC, "name"));
        orders.add(new Order(Direction.ASC, "distance.a"));
        Sort sort = new Sort(orders);
        pageable.setSort(sort);
        List<Category> data = categoryDao.findByPage(pageable);
        JSONObject jsonObject = JSONFactory.getSuccessJSON();
        jsonObject.put("data", data);
        return jsonObject;
    }
    
    public JSON findDetailById(String id) {
        Category data = categoryRepository.findOne(id);
        if(data==null) {
            return JSONFactory.getfailJSON("未找到品类");
        }
        JSONObject jsonObject = JSONFactory.getSuccessJSON();
        jsonObject.put("data", data);
        return jsonObject;
    }
    
    public JSON updateDetailById(String id, String update) {
        if(update!=null) {
            categoryDao.updateById(id, update, Category.class);
        }
        return JSONFactory.getSuccessJSON();
    }
    
    public JSON findByDistance(Double fromDistance, Double toDistance) {
        List<Category> data = categoryDao.findByDistance(fromDistance, toDistance);
        if(data==null) {
            return JSONFactory.getfailJSON("未找到品类");
        }
        JSONObject jsonObject = JSONFactory.getSuccessJSON();
        jsonObject.put("data", data);
        return jsonObject;
    }
    
    public JSON findAdditionsByCategoryId(String id) {
        Category data = categoryRepository.findOne(id);
        if(data==null) {
            return JSONFactory.getfailJSON("未找到品类");
        }
        JSONObject jsonObject = JSONFactory.getSuccessJSON();
        jsonObject.put("data", data.getAddition());
        return jsonObject;
    }
    
    public JSON calculateWeightPrice(String id, Double weight) {
        BigDecimal price = BigDecimal.ZERO;
        JSONObject json = JSONFactory.getSuccessJSON();
        if(weight==null) {
           json.put("data", price);
           return json;
        }
        Category category = categoryDao.findWeightPriceRuleById(id, weight);
        if(category!=null && category.getWeightLevel()!=null
                && category.getWeightLevel().size()>0) {
            List<RaisePrice> weights = category.getWeightLevel();
            price = weights.get(0).getPriceDelta();
        }
        json.put("data", price);
        return json;
    }
}
