package com.tairitsu.ignotus.cache

import java.time.Duration
import java.time.LocalDateTime
import java.util.function.Supplier

interface CacheService {
    /**
     * Retrieve an item from the cache and delete it.
     *
     * @param [key]
     * @param [type]
     * @param [default]
     * @return
     */
    fun <T> pull(key: String, type: Class<T>, default: T?): T?

    /**
     * Retrieve an item from the cache and delete it.
     *
     * @param [key]
     * @param [type]
     * @return
     */
    fun <T> pull(key: String, type: Class<T>): T? = pull(key, type, null)

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return bool
     */
    fun <T> put(key: String, value: T): Boolean

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    fun <T> put(key: String, value: T, ttl: Long): Boolean

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    fun <T> put(key: String, value: T, ttl: Duration): Boolean

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @param  [expiresAt]
     * @return bool
     */
    fun <T> put(key: String, value: T, expiresAt: LocalDateTime): Boolean

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    fun <T> add(key: String, value: T, ttl: Long): Boolean

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [ttl]
     * @return bool
     */
    fun <T> add(key: String, value: T, ttl: Duration): Boolean

    /**
     * Store an item in the cache if the key does not exist.
     *
     * @param  [key]
     * @param  [value]
     * @param  [expiresAt]
     * @return bool
     */
    fun <T> add(key: String, value: T, expiresAt: LocalDateTime): Boolean

    /**
     * Increment the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    fun increment(key: String, value: Long): Long?

    /**
     * Decrement the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    fun decrement(key: String, value: Long): Long?

    /**
     * Store an item in the cache indefinitely.
     *
     * @param  key
     * @param  value
     * @return bool
     */
    fun <T> forever(key: String, value: T): Boolean = put(key, value)

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  type
     * @param  callback
     * @return
     */
    fun <T> remember(key: String, ttl: Long, type: Class<T>, callback: Supplier<T>): T?

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  type
     * @param  callback
     * @return
     */
    fun <T> remember(key: String, ttl: Duration, type: Class<T>, callback: Supplier<T>): T?

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  expiresAt
     * @param  type
     * @param  callback
     * @return
     */
    fun <T> remember(key: String, expiresAt: LocalDateTime, type: Class<T>, callback: Supplier<T>): T?

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  type
     * @param  callback
     * @return
     */
    fun <T> sear(key: String, type: Class<T>, callback: Supplier<T>): T? = remember(key, type, callback)

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  callback
     * @param  type
     * @return mixed
     */
    fun <T> rememberForever(key: String, type: Class<T>, callback: Supplier<T>): T? = remember(key, type, callback)

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  callback
     * @param  type
     * @return mixed
     */
    fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T?

    /**
     * Remove an item from the cache.
     *
     * @param  key
     * @return bool
     */
    fun forget(key: String): Boolean

    /**
     * Check if a key exists.
     *
     * @param  key
     * @return bool
     */
    fun has(key: String): Boolean

    /**
     * Retrieve an item from the cache.
     *
     * @param [key]
     * @param [type]
     * @param [default]
     * @return
     */
    fun <T> get(key: String, type: Class<T>, default: T?): T?

    /**
     * Retrieve an item from the cache.
     *
     * @param [key]
     * @param [type]
     * @return
     */
    fun <T> get(key: String, type: Class<T>): T? = get(key, type, null)
}
