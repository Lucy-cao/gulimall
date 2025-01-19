package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.order.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
	@Autowired
	private LoginInterceptor loginInterceptor;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
//		registry.addViewController("/confirm.html").setViewName("confirm");
		registry.addViewController("/detail.html").setViewName("detail");
//		registry.addViewController("/list.html").setViewName("list");
		registry.addViewController("/pay.html").setViewName("pay");
	}

	/**
	 * 将拦截器添加到web配置中
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
	}
}
