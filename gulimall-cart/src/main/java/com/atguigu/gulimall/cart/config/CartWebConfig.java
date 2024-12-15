package com.atguigu.gulimall.cart.config;

import com.atguigu.gulimall.cart.interceptor.CartLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CartWebConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CartLoginInterceptor()).addPathPatterns("/**");
	}
}
