package com.tairitsu.ignotus.cache.redis

import com.tairitsu.ignotus.cache.CacheService
import com.tairitsu.ignotus.support.util.JSON.jsonToObject
import com.tairitsu.ignotus.support.util.JSON.toJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


class RedisCacheService(private val redisTemplate: RedisTemplate<String, String>) : CacheService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val LOCK_PREFIX = "_LOCK:"
        private val LOCK_CAPACITY = Duration.ofSeconds(30).toMillis()
    }

    private fun setLock(key: String): Boolean {
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

    private fun releaseLock(key: String): Boolean {
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

    private inline fun lock(key: String, block: () -> Unit) {
        while (true) {
            if (setLock(key)) {
                block()
                releaseLock(key)
                break
            }
        }
    }

    /**
     * Retrieve an item from the cache and delete it.
     *
     * @param [key]
     * @param [type]
     * @param [default]
     * @return
     */
    override fun <T> pull(key: String, type: Class<T>, default: T?): T? {
        var ret: T? = null

        lock(key) {
            val value = redisTemplate.opsForValue().get(key)
            if (value != null) {
                redisTemplate.opsForValue().operations.delete(key)
                ret = value.jsonToObject(type)
            }
        }
        return ret
    }

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return bool
     */
    override fun <T> put(key: String, value: T): Boolean {
        redisTemplate.opsForValue().set(key, value?.toJson() ?: "null")
        return true
    }

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    override fun <T> put(key: String, value: T, ttl: Long): Boolean {
        redisTemplate.opsForValue().set(key, value?.toJson() ?: "null", ttl, TimeUnit.SECONDS)
        return true
    }

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    override fun <T> put(key: String, value: T, ttl: Duration): Boolean {
        redisTemplate.opsForValue().set(key, value?.toJson() ?: "null", ttl)
        return true
    }

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [expiresAt]
     * @return bool
     */
    override fun <T> put(key: String, value: T, expiresAt: LocalDateTime): Boolean {
        val duration = Duration.between(LocalDateTime.now(), expiresAt)
        redisTemplate.opsForValue().set(key, value?.toJson() ?: "null", duration)
        return true
    }

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    override fun <T> add(key: String, value: T, ttl: Long): Boolean {
        var ret = false
        lock(key) {
            if (redisTemplate.opsForValue().setIfAbsent(key, value?.toJson() ?: "null", ttl, TimeUnit.SECONDS) == true) {
                ret = true
            }
        }
        return ret
    }

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    override fun <T> add(key: String, value: T, ttl: Duration): Boolean {
        var ret = false
        lock(key) {
            if (redisTemplate.opsForValue().setIfAbsent(key, value?.toJson() ?: "null", ttl) == true) {
                ret = true
            }
        }
        return ret
    }

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [expiresAt]
     * @return bool
     */
    override fun <T> add(key: String, value: T, expiresAt: LocalDateTime): Boolean {
        val duration = Duration.between(LocalDateTime.now(), expiresAt)
        var ret = false
        lock(key) {
            if (redisTemplate.opsForValue().setIfAbsent(key, value?.toJson() ?: "null", duration) == true) {
                ret = true
            }
        }
        return ret
    }

    /**
     * Increment the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun increment(key: String, value: Long): Long? {
        return redisTemplate.opsForValue().increment(key, value)
    }

    /**
     * Decrement the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun decrement(key: String, value: Long): Long? {
        return redisTemplate.opsForValue().decrement(key, value)
    }

    /**
     * Store an item in the cache indefinitely.
     *
     * @param  key
     * @param  value
     * @return bool
     */
    override fun <T> forever(key: String, value: T): Boolean {
        redisTemplate.opsForValue().set(key, value?.toJson() ?: "null")
        return true
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  type
     * @param  callback
     * @return
     */
    override fun <T> remember(key: String, ttl: Long, type: Class<T>, callback: Supplier<T>): T? {
        return remember(key, Duration.ofSeconds(ttl), type, callback)
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  type
     * @param  callback
     * @return
     */
    override fun <T> remember(key: String, ttl: Duration, type: Class<T>, callback: Supplier<T>): T? {
        var ret: T? = null
        lock(key) {
            if (redisTemplate.hasKey(key)) {
                val value = redisTemplate.opsForValue().get(key)
                ret = value?.jsonToObject(type)
            }

            if (ret == null) {
                val value = callback.get()
                redisTemplate.opsForValue().set(key, value?.toJson() ?: "null", ttl)
                ret = value
            }
        }
        return ret
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  expiresAt
     * @param  type
     * @param  callback
     * @return
     */
    override fun <T> remember(key: String, expiresAt: LocalDateTime, type: Class<T>, callback: Supplier<T>): T? {
        val duration = Duration.between(LocalDateTime.now(), expiresAt)
        return remember(key, duration, type, callback)
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  type
     * @param  callback
     * @return mixed
     */
    override fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T? {
        var ret: T? = null
        lock(key) {
            if (redisTemplate.hasKey(key)) {
                val value = redisTemplate.opsForValue().get(key)
                ret = value?.jsonToObject(type)
            }

            if (ret == null) {
                val value = callback.get()
                redisTemplate.opsForValue().set(key, value?.toJson() ?: "null")
                ret = value
            }
        }
        return ret
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  type
     * @param  callback
     * @return
     */
    override fun <T> sear(key: String, type: Class<T>, callback: Supplier<T>): T? {
        return remember(key, type, callback)
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  type
     * @param  callback
     * @return mixed
     */
    override fun <T> rememberForever(key: String, type: Class<T>, callback: Supplier<T>): T? {
        return remember(key, type, callback)
    }

    /**
     * Remove an item from the cache.
     *
     * @param  key
     * @return bool
     */
    override fun forget(key: String): Boolean {
        return redisTemplate.opsForValue().operations.delete(key) == true
    }

    /**
     * Check if a key exists.
     *
     * @param  key
     * @return bool
     */
    override fun has(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    /**
     * Retrieve an item from the cache.
     *
     * @param [key]
     * @param [type]
     * @param [default]
     * @return
     */
    override fun <T> get(key: String, type: Class<T>, default: T?): T? {
        val value = redisTemplate.opsForValue().get(key)
        return value?.jsonToObject(type) ?: default
    }
}
