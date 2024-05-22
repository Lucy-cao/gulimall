package com.atguigu.gulimall.ware;

import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.service.WareInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallWareApplicationTests {
    @Autowired
    WareInfoService wareInfoService;
    @Test
    void contextLoads() {
        WareInfoEntity wareInfo=new WareInfoEntity();
        wareInfo.setName("杭州的仓库");
        wareInfoService.save(wareInfo);
    }

}
