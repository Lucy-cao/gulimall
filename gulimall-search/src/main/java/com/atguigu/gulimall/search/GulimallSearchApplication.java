package com.atguigu.gulimall.search;

import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssContextAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.atguigu.gulimall.search.feign")
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, OssContextAutoConfiguration.class})
public class GulimallSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallSearchApplication.class, args);
	}

}
