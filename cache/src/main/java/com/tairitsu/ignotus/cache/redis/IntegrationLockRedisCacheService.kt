package com.tairitsu.ignotus.cache.redis

import com.tairitsu.ignotus.cache.exception.GetLockFailedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.integration.redis.util.RedisLockRegistry
import java.util.concurrent.TimeUnit


class IntegrationLockRedisCacheService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisLockRegistry: RedisLockRegistry,
) : RedisCacheService(redisTemplate) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val LOCK_PREFIX = "_LOCK"
    }

    override fun lock(key: String, block: () -> Unit) {
        val lock = redisLockRegistry.obtain(key)
        var isOwnLock = false
        try {
            isOwnLock = lock.tryLock(3, TimeUnit.SECONDS)
            if (isOwnLock) {
                return block()
            }
        } catch (e: InterruptedException) {
            throw GetLockFailedException("Failed to get lock $key", e)
        } finally {
            if (isOwnLock) {
                lock.unlock()
            }
        }
        throw GetLockFailedException("Failed to get lock $key", null)
    }
}
