package com.tech.product_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.tech.product_service.dto.ProductResponse;

@Configuration
@EnableCaching
public class RedisConfig {
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
	    RedisSerializer<ProductResponse> serializer =
	            new Jackson2JsonRedisSerializer<>(ProductResponse.class);

	    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
	            .serializeValuesWith(
	                    RedisSerializationContext.SerializationPair.fromSerializer(serializer)
	            );

	    return RedisCacheManager.builder(factory)
	            .cacheDefaults(config)
	            .build();
	}

}

