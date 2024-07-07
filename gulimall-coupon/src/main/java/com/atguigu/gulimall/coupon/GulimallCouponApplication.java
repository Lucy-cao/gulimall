package com.atguigu.gulimall.coupon;

import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssContextAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com/atguigu/gulimall/coupon/dao")
@SpringBootApplication(exclude = {OssContextAutoConfiguration.class})
@EnableDiscoveryClient
@EnableTransactionManagement
public class GulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }
}
