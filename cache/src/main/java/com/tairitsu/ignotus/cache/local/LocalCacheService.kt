package com.tairitsu.ignotus.cache.local

import com.github.benmanes.caffeine.cache.Cache
import com.tairitsu.ignotus.cache.CacheService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.function.Supplier

/**
 * use caffeine
 */
class LocalCacheService(private val cache: Cache<String, String>): CacheService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun <T> pull(key: String, type: Class<T>, default: T?): T? {
        TODO("Not yet implemented")
    }

    override fun <T> put(key: String, value: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> put(key: String, value: T, ttl: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> put(key: String, value: T, ttl: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> put(key: String, value: T, expiresAt: LocalDateTime): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> add(key: String, value: T, ttl: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> add(key: String, value: T, ttl: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> add(key: String, value: T, expiresAt: LocalDateTime): Boolean {
        TODO("Not yet implemented")
    }

    override fun increment(key: String, value: Long): Long? {
        TODO("Not yet implemented")
    }

    override fun decrement(key: String, value: Long): Long? {
        TODO("Not yet implemented")
    }

    override fun <T> forever(key: String, value: T): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> remember(key: String, ttl: Long, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T> remember(key: String, ttl: Duration, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T> remember(key: String, expiresAt: LocalDateTime, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T> sear(key: String, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun <T> rememberForever(key: String, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    override fun forget(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun has(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T> get(key: String, type: Class<T>, default: T?): T? {
        TODO("Not yet implemented")
    }
}
