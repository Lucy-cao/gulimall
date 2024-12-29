package com.atguigu.gulimall.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/confirm.html").setViewName("confirm");
		registry.addViewController("/detail.html").setViewName("detail");
		registry.addViewController("/list.html").setViewName("list");
		registry.addViewController("/pay.html").setViewName("pay");
	}
}
