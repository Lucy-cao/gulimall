package com.atguigu.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gulimall.thread")
public class ThreadPoolConfigProperties {
	private Integer coreSize;
	private Integer maxSize;
	private Integer keepAliveTime; // 单位秒
}
