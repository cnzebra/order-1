package com.mrwind.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
    @RequestMapping(value="findByPage", method = RequestMethod.GET)
    public JSON findByPage(Integer pageno, Integer pagesize) {
        return categoryService.findAllByPage(pageno, pagesize);
    }
    
    @ResponseBody
    @RequestMapping(value="findDetailById", method = RequestMethod.GET)
    public JSON findDetailById(@RequestParam(value="id", required=true) String id) {
        return categoryService.findDetailById(id);
    }
    
    @ResponseBody
    @RequestMapping(value="update", method = RequestMethod.PUT)
    public JSON update(
            @RequestParam(value="id", required=true) String id, 
            @RequestBody String body) {
        return categoryService.updateDetailById(id, body);
    }
    
    /**
     * 根据距离跨度查找品类
     */
    @ResponseBody
    @RequestMapping(value="findCategoryByDistance", method = RequestMethod.GET)
    public JSON getAdditionsByCategory(
            @RequestParam(value="fromDistance", required=true) Double fromDistance,
            @RequestParam(value="toDistance", required=true) Double toDistance) {
        return categoryService.findByDistance(fromDistance, toDistance);
    }
    
    /**
     * 根据ID获得品类的特殊要求
     */
    @ResponseBody
    @RequestMapping(value="findAdditionsByCategoryId", method = RequestMethod.GET)
    public JSON findAdditionsByCategoryId(@RequestParam(value="id", required=true) String id) {
        return categoryService.findAdditionsByCategoryId(id);
    }
}
