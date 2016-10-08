package com.mrwind.category.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mrwind.category.dao.ShopCategoryDao;
import com.mrwind.category.remote.windCloud.api.WindCloudBaseApi;
import com.mrwind.common.factory.JSONFactory;

@Service
public class ShopCategoryService {

    @Autowired
    private ShopCategoryDao shopCategoryDao;
    @Autowired
    private WindCloudBaseApi windCloudBaseApi;
    
    public JSON operateShopCategoryRelation(String shopToken, String body, HttpServletResponse response) {
        String shopId = windCloudBaseApi.getUserIdByToken(shopToken, response);
        if(StringUtils.isBlank(shopId)) {
            response.setStatus(401);
            return JSONFactory.getfailJSON("用户Token错误！");
        }
        List<String> categorys = JSON.parseArray(body, String.class);
        if(categorys==null || categorys.size()==0) {
            return JSONFactory.getfailJSON("没有选择品类");
        }
        shopCategoryDao.upsertCategorysByShopId(shopId, categorys);
        return JSONFactory.getSuccessJSON();
    }
}
