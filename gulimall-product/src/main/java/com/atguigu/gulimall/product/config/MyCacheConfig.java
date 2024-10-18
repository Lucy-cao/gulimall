package com.atguigu.gulimall.product.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 自定义缓存相关的配置
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {
	@Bean
	RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		//配置redis的key序列化方式为字符串
		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
		//配置redis的value序列化方式为json
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		//需要支持从配置文件读取配置项。需要指定当前配置类所需要绑定的配置文件
		CacheProperties.Redis redisProperties = cacheProperties.getRedis();
		//获取配置文件的过期时间ttl
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		//获取配置的前缀
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
		}
		//设置是否允许保存null值
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		//设置是否使用前缀
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}

		return config;
	}
}
