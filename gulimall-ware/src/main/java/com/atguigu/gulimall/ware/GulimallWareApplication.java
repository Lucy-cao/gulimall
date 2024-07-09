package com.atguigu.gulimall.ware;

import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssContextAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com/atguigu/gulimall/ware/dao")
@SpringBootApplication(exclude = {OssContextAutoConfiguration.class})
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.atguigu.gulimall.ware.feign")
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
