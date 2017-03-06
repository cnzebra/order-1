package com.mrwind.order.repositories;

import com.mrwind.order.entity.ExpressCodeLog;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by hakan on 3/6/17.
 */
public interface ExpressCodeLogRepository extends PagingAndSortingRepository<ExpressCodeLog,String> {
}
