package com.mrwind.category.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.category.service.ShopCategoryService;
import com.mrwind.common.factory.JSONFactory;

@Controller("shopCategory/")
public class ShopCategoryController {
    
    @Autowired
    private ShopCategoryService shopCategoryService;
    
    @ResponseBody
    @RequestMapping(value="relation/update")
    public JSON updateRelation(@RequestBody String body, 
            HttpServletRequest request, 
            HttpServletResponse response) {
        if(body==null) {
            return JSONFactory.getfailJSON("参数错误");
        }
        String auth = request.getHeader("Authorization");//获取安卓token
        String token = auth.substring(auth.indexOf(' ')+1);
        if (StringUtils.isBlank(token)) {
            response.setStatus(401);
            return JSONFactory.getfailJSON("用户Token错误！");
        }
        
        return shopCategoryService.operateShopCategoryRelation(token, body, response);
    }
}
