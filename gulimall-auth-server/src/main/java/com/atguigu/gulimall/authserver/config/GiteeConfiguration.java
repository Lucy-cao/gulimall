package com.atguigu.gulimall.authserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("gulimall.oauth2.gitee")
public class GiteeConfiguration {
	private String clientId;
	private String clientSecret;
}
