package com.mrwind.order;

import com.mrwind.common.util.HttpUtil;
import com.mrwind.order.service.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by meijingxiang on 2017/3/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring-application.xml"})
public class CommonTest {
    @Autowired
    OrderService orderService;

    @Test
    public void asd(){
        HttpUtil.sendSMSToUserTel("asdasd", "18368404766");
    }
}
