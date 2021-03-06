package com.mrwind.order.repositories;


import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.mrwind.order.entity.Express;

public interface  ExpressRepository extends QueryByExampleExecutor<Express>,PagingAndSortingRepository<Express,String>{

	Express findFirstByExpressNo(String expressNo);

	Express findFirstByBindExpressNo(String bindExpressNo);

	List<Express> findByExpressNoIn(Collection<String> express);

	List<Express> findByExpressNoIn(Collection<String> express,Sort sort);
	
	Page<Express> findByShopId(ObjectId shopId,Pageable pageable);

	Page<Express> findByShopId(String shopId, Pageable pageable);
	
	List<Express> findBySubStatus(String subStatus);
	
	Long countByShopId(ObjectId shopId);
	
	Long countByShopId(String shopId);
	
	Long countByShopIdAndStatus(ObjectId shopId,String status);
	
	Long countByShopIdAndStatus(String shopId,String status);
	
	List<Express> findAllByDueTimeGreaterThanEqual(Date beginTime);
}
