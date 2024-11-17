package com.atguigu.gulimall.authserver;

import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssAutoConfiguration;
import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssContextAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession //整合redis保存session
@EnableFeignClients(basePackages = "com.atguigu.gulimall.authserver.feign")
@EnableDiscoveryClient
@SpringBootApplication(exclude = {OssContextAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class GulimallAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallAuthServerApplication.class, args);
	}

}
