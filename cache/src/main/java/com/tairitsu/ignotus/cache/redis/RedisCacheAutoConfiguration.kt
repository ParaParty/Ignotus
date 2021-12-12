package com.tairitsu.ignotus.cache.redis

import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate

@Configuration("IgnotusRedisCacheAutoConfiguration")
@ConditionalOnClass(RedisOperations::class, RedisTemplate::class)
@EnableConfigurationProperties(RedisProperties::class)
@AutoConfigureAfter(RedisAutoConfiguration::class)
open class RedisCacheAutoConfiguration {
    @Bean("IgnotusRedisCache")
    @ConditionalOnProperty(prefix = "ignotus.cache.auto-configuration", name = ["enabled"], havingValue = "true", matchIfMissing = true)
    open fun redisCache(redisTemplate: RedisTemplate<String, String>): RedisCacheService {
        return RedisCacheService(redisTemplate)
    }
}
