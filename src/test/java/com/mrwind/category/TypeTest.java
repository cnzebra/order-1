package com.mrwind.category;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.mrwind.category.repositories.TypeRepository;
import com.mrwind.order.entity.Order;
import com.mrwind.order.service.OrderService;

@RunWith(org.springframework.test.context.junit4.SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-*.xml" })
public class TypeTest {
    
    @Autowired
    private OrderService orderService;

    @Test
    public void save() {
        Order order = new Order();
        orderService.select(order);
    }
}
