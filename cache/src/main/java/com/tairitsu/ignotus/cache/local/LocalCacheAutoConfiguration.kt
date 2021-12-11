package com.tairitsu.ignotus.cache.local

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class LocalCacheAutoConfiguration {
    @Bean
    open fun localCache(): LocalCacheService {
        return LocalCacheService(
            Caffeine.newBuilder()
                .initialCapacity(1_000)
                .maximumSize(1000_000)
                .build()
        )
    }
}
