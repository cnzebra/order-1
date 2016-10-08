package com.mrwind.category.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mrwind.category.dao.base.BaseDao;
import com.mrwind.category.entity.ShopCategory;

@Repository
public class ShopCategoryDao extends BaseDao {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public String selectIdByShopId(String shopId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("shopId").is(shopId)
                );
        query.addCriteria(criteria);
        query.fields().include("id");
        return mongoTemplate.findOne(query, String.class, "category");
    }
    
    /**
     * 查询shopId没有插入新的，有更新categorys
     * @param shopId
     * @param categorys
     */
    public void upsertCategorysByShopId(String shopId, List<String> categorys) {
        Update update = new Update();
        update.set("categorys", categorys);
        mongoTemplate.upsert(Query.query(Criteria.where("shopId").is(shopId)), 
                update, ShopCategory.class, "category");
        
    }
}
