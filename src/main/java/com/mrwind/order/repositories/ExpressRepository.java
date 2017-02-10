package com.mrwind.order.repositories;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.mrwind.order.entity.Express;

public interface ExpressRepository extends QueryByExampleExecutor<Express>,PagingAndSortingRepository<Express,ObjectId>{

	Express findFirstByExpressNo(Long expressNo);

	List<Express> findByExpressNoIn(List<Long> express);
	
	Page<Express> findByShopId(String shopId,Pageable pageable);
}
