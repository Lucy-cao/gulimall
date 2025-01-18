package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
	//通过自定义请求拦截器，在openfeign请求时加入请求头等需要的参数
	@Bean
	public RequestInterceptor requestInterceptor() {
		return new RequestInterceptor() {

			@Override
			public void apply(RequestTemplate requestTemplate) {
				//在请求头中加入原有web请求的请求头
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				//获取到请求对象
				HttpServletRequest request = requestAttributes.getRequest();
				String cookie = request.getHeader("cookie");
				//将cookie设置到新构建的feign请求头中
				requestTemplate.header("cookie", cookie);
			}
		};
	}
}
