package com.mrwind.category.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mrwind.category.dao.base.BaseDao;
import com.mrwind.category.entity.Category;
import com.mrwind.common.bean.SpringDataPageable;

@Repository
public class CategoryDao extends BaseDao{

    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 找到名字和距离重合的模板
     * 条件是mongodb中：1.name相同，2.distance.a<=toDistance 并且 distance.b>=fromDistance
     * @param name
     * @param fromDistance
     * @param toDistance
     * @return
     */
    public Long countByNameAndDistance(String name, Double fromDistance, Double toDistance) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("name").is(name),
                Criteria.where("distance.a").lt(toDistance),
                Criteria.where("distance.b").gt(fromDistance)
                );
        query.addCriteria(criteria);
        return mongoTemplate.count(query, "category");
    }
    
    /**
     * 分页查询
     */
    public List<Category> findByPage(SpringDataPageable pageable) {
        Query query = new Query();  
        query.with(pageable);
        List<Category> data = mongoTemplate.find(query, Category.class);
        return data;
    }
    
}
