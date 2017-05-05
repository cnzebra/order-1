package com.mrwind.order.repositories;


import com.mrwind.order.entity.OrderTransfer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface OrderTransferRepository extends QueryByExampleExecutor<OrderTransfer>,PagingAndSortingRepository<OrderTransfer,String> {

}
