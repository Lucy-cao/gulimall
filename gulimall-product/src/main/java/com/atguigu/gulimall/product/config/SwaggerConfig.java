package com.atguigu.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
	@Bean
	public Docket ApiConfig() {
		return new Docket(DocumentationType.SWAGGER_2)
				.enable(true)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.atguigu.gulimall.product.controller"))
				.paths(PathSelectors.any())
				.build();
	}

	public ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("谷粒商城商品服务接口文档")
				.description("商品服务")
				.version("1.0")
				.contact(new Contact("Lucy", "https://blog.lucyoolife.top/", "xxx@163.com"))
				.build();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
				.resourceChain(false);
		//swagger访问路径：http://127.0.0.1:10000/swagger-ui/index.html#/
	}
}
