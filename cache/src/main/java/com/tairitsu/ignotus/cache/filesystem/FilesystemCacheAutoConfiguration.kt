package com.tairitsu.ignotus.cache.filesystem

import com.tairitsu.ignotus.cache.CacheConfig
import com.tairitsu.ignotus.cache.CacheService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration

@Configuration("IgnotusFilesystemCacheAutoConfiguration")
@ConditionalOnMissingBean(CacheService::class)
open class FilesystemCacheAutoConfiguration {
    @Bean("IgnotusFilesystemCache")
    @Conditional(value = [FilesystemAutoConfigurationCondition::class])
    open fun FilesystemCache(config: CacheConfig): FilesystemCacheService {
        return FilesystemCacheService(config.autoConfiguration.storagePath)
    }
}
