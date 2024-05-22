package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallOrderApplicationTests {
    @Autowired
    OrderService orderService;
    @Test
    void contextLoads() {
        OrderEntity order=new OrderEntity();
        order.setMemberId(23L);
        orderService.save(order);
    }

}
