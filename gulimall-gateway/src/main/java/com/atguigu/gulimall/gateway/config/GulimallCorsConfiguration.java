package com.atguigu.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class GulimallCorsConfiguration {
    @Bean //加入到容器中
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        //配置跨域
        config.addAllowedHeader("*");//允许任意请求头跨域
        config.addAllowedOriginPattern("*");//允许任意请求来源跨域
        config.addAllowedMethod("*");//允许所有请求方式跨域
        config.setAllowCredentials(true);//允许是否携带cookie信息跨域

        //注册跨域的配置：任意路径都要跨域配置
        source.registerCorsConfiguration("/**",config);
        return new CorsWebFilter(source);
    }

}
