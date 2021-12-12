package com.tairitsu.ignotus.cache.mongo

import com.tairitsu.ignotus.cache.CacheService
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.MongoTemplate

@Configuration("IgnotusMongoCacheAutoConfiguration")
@ConditionalOnClass(MongoOperations::class, MongoTemplate::class)
@EnableConfigurationProperties(MongoProperties::class)
@AutoConfigureAfter(MongoAutoConfiguration::class)
open class MongoCacheAutoConfiguration() {
    @Bean("IgnotusMongoCacheService")
    open fun mongoCache(mongoTemplate: MongoTemplate): CacheService {
        return MongoCacheService(mongoTemplate)
    }
}
