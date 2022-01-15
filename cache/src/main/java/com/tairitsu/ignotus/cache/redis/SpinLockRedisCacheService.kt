package com.tairitsu.ignotus.cache.redis

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.*


class SpinLockRedisCacheService(private val redisTemplate: RedisTemplate<String, String>) :
    RedisCacheService(redisTemplate) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val LOCK_PREFIX = "_LOCK:"
        private val LOCK_CAPACITY = Duration.ofSeconds(30).toMillis()
    }

    fun setLock(key: String): Boolean {
        val lockKey = LOCK_PREFIX + key

        val now: Long = Date().time
        val nowStr = now.toString()
        if (redisTemplate.opsForValue().setIfAbsent(lockKey, nowStr) == true) {
            return true
        }

        val lastLock = redisTemplate.opsForValue().get(lockKey)?.toLong() ?: return false

        if (lastLock + LOCK_CAPACITY < now) {
            redisTemplate.opsForValue().set(lockKey, nowStr)
            return true
        }

        return false
    }

    fun releaseLock(key: String): Boolean {
        val lockKey = LOCK_PREFIX + key
        try {
            redisTemplate.opsForValue().get(lockKey)?.toLong() ?: return false
            redisTemplate.opsForValue().operations.delete(lockKey)
            return true
        } catch (e: Exception) {
            log.error(e.message, e)
            return false
        }
    }

    override fun lock(key: String, block: () -> Unit) {
        while (true) {
            if (setLock(key)) {
                block()
                releaseLock(key)
                break
            }
        }
    }
}
