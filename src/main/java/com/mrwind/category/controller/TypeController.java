package com.mrwind.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.service.TypeService;

@Controller
@RequestMapping("type/")
public class TypeController {

    @Autowired
    private TypeService typeService;
    
    @ResponseBody
    @RequestMapping(value="save", method = RequestMethod.POST)
    public JSON insert(
            @RequestParam(value="name", required=true) String name) {
        return typeService.newCategoryType(name);
    }
}
