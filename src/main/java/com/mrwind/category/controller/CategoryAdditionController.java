package com.mrwind.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.entity.CategoryAddition;
import com.mrwind.category.service.CategoryAdditionService;

@Controller
@RequestMapping("categoryAddition/")
public class CategoryAdditionController {
    @Autowired
    private CategoryAdditionService categoryAdditionService;

    @ResponseBody
    @RequestMapping(value="save", method = RequestMethod.POST)
    public JSON newCategoryAddition(@RequestBody CategoryAddition addition) {
        return categoryAdditionService.save(addition);
    }
    
    @ResponseBody
    @RequestMapping(value="findAll", method = RequestMethod.GET)
    public JSON findAll() {
        return categoryAdditionService.getAllNotDel();
    }
}
