package com.atguigu.gulimall.coupon;

import com.atguigu.gulimall.coupon.entity.HomeSubjectEntity;
import com.atguigu.gulimall.coupon.service.HomeSubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallCouponApplicationTests {
    @Autowired
    HomeSubjectService homeSubjectService;
    @Test
    void contextLoads() {
        HomeSubjectEntity homeSubjectEntity=new HomeSubjectEntity();
        homeSubjectEntity.setName("手机");
        homeSubjectService.save(homeSubjectEntity);
    }

}
