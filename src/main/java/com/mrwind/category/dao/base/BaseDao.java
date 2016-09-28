package com.mrwind.category.dao.base;

import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.fastjson.JSONObject;

public class BaseDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void updateById(String id, String body, Class<?> entityClass){
        Update update = new Update();
        setUpdateByJSON(body, update);
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("id").is(id)), 
                update,
                entityClass);
    }
    
    protected void setUpdateByJSON(final String body, final Update update) {
        JSONObject jsonObject = JSONObject.parseObject(body) ;  
        if(jsonObject!=null) {
            for(Entry<String, Object> entry : jsonObject.entrySet()) {
                Object value = entry.getValue();
                String key = entry.getKey();
                if(value!=null) {
                    update.set(key, value);
                }
            }
        }
    }
}
