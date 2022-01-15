package com.tairitsu.ignotus.cache

import com.tairitsu.ignotus.cache.filesystem.FilesystemCacheService
import com.tairitsu.ignotus.cache.mongo.MongoCacheService
import com.tairitsu.ignotus.cache.redis.IntegrationLockRedisCacheService
import com.tairitsu.ignotus.cache.redis.SpinLockRedisCacheService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.integration.redis.util.RedisLockRegistry

@Configuration("IgnotusCacheAutoConfiguration")
@ComponentScan("com.tairitsu.ignotus.cache")
open class AutoConfiguration {

    @Bean("IgnotusCacheService")
    @ConditionalOnProperty(prefix = "ignotus.cache.auto-configuration",
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = true)
    open fun ignotusCacheService(app: ApplicationContext, config: CacheConfig): CacheService {
        val setType = config.autoConfiguration.type.lowercase()
        return when (guessType(app, setType)) {
            "redis" -> try {
                val redisConnectionFactory = app.getBean(RedisConnectionFactory::class.java)
                val redisLockRegistry = RedisLockRegistry(redisConnectionFactory, IntegrationLockRedisCacheService.LOCK_PREFIX)
                IntegrationLockRedisCacheService(app.getBean(StringRedisTemplate::class.java), redisLockRegistry)
            } catch (e: Throwable) {
                SpinLockRedisCacheService(app.getBean(StringRedisTemplate::class.java))
            }
            "mongo" -> MongoCacheService(app.getBean(MongoTemplate::class.java))
            "filesystem" -> FilesystemCacheService(config.autoConfiguration.storagePath)
            else -> throw IllegalArgumentException()
        }
    }

    private fun guessType(app: ApplicationContext, type: String): String {
        if (type != "auto") {
            return type
        }

        try {
            app.getBean(StringRedisTemplate::class.java)
            return "redis"
        } catch (ignored: Throwable) {

        }

        try {
            app.getBean(MongoTemplate::class.java)
            return "mongo"
        } catch (ignored: Throwable) {

        }

        return "filesystem"
    }
}
