package com.mrwind.order.repositories;

import com.mrwind.order.entity.Remind;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * <pre>
 *     author : huanghaikai
 *     e-mail : hakanhuang@gmail.com
 *     time   : 2017/04/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public interface RemindRepository extends PagingAndSortingRepository<Remind,String> {
    Remind findByExpressNo(String expressNo);
}
