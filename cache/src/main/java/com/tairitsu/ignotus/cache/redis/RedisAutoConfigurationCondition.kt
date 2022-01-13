package com.tairitsu.ignotus.cache.redis

import com.tairitsu.ignotus.cache.AutoConfigurationCondition

internal class RedisAutoConfigurationCondition : AutoConfigurationCondition() {
    override val type = listOf("redis")
}
