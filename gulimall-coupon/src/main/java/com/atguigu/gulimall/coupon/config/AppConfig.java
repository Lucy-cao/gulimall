package com.atguigu.gulimall.coupon.config;

import com.baomidou.mybatisplus.core.toolkit.Sequence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public Sequence sequence() {
        return new Sequence(1L, 2L);
    }
}
