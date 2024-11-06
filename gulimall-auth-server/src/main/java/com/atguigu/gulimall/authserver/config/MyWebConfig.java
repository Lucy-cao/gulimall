package com.atguigu.gulimall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//将对应的路由和页面的名字写进去即可，多个页面就注册多条
		registry.addViewController("/login.html").setViewName("login");
		registry.addViewController("/register.html").setViewName("register");
	}
}
