package com.mrwind.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.entity.Category;
import com.mrwind.category.service.CategoryService;

@Controller
@RequestMapping("category/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    @ResponseBody
    @RequestMapping(value="save", method = RequestMethod.POST)
    public JSON insert(@RequestBody Category category) {
        return categoryService.newCategory(category);
    }
    
    @ResponseBody
    @RequestMapping(value="find", method = RequestMethod.GET)
    public Long find(String name, Double fromDistance, Double toDistance) {
        return categoryService.countByModel(name, fromDistance, toDistance);
    }
}
