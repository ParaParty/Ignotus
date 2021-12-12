package com.tairitsu.ignotus.cache

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component("IgnotusCacheConfig")
@ConfigurationProperties(prefix = "ignotus.cache")
open class CacheConfig {
    var autoConfiguration: CacheAutoConfigurationConfig = CacheAutoConfigurationConfig()
}

open class CacheAutoConfigurationConfig {
    var enabled: Boolean = true
}
