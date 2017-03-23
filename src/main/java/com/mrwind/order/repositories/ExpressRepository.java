package com.mrwind.order.repositories;


import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.mrwind.order.entity.Express;

public interface ExpressRepository extends QueryByExampleExecutor<Express>,PagingAndSortingRepository<Express,String>{

	Express findFirstByExpressNo(String expressNo);
	
	Express findFirstByBindExpressNo(String bindExpressNo);

	List<Express> findByExpressNoIn(Collection<String> express);
	
	Page<Express> findByShopId(ObjectId shopId,Pageable pageable);

	Page<Express> findByShopId(String shopId, Pageable pageable);
	
	List<Express> findBySubStatus(String subStatus);
}
