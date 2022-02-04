package com.tairitsu.ignotus.cache

import java.time.Duration
import java.time.LocalDateTime
import java.util.function.Supplier

object CacheServiceExtension {
    /**
     * Retrieve an item from the cache and delete it.
     *
     * @param [key]
     * @param [default]
     * @return
     */
    inline fun <reified T> CacheService.pull(key: String, default: T? = null): T? = pull(key, T::class.java, default)

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  callback
     * @return
     */
    inline fun <reified T> CacheService.remember(key: String, ttl: Long, callback: Supplier<T>): T? =
        remember(key, ttl, T::class.java, callback)

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  ttl
     * @param  callback
     * @return
     */
    inline fun <reified T> CacheService.remember(key: String, ttl: Duration, callback: Supplier<T>): T? =
        remember(key, ttl, T::class.java, callback)

    /**
     * Get an item from the cache, or execute the given Closure and store the result.
     *
     * @param  key
     * @param  expiresAt
     * @param  callback
     * @return
     */
    inline fun <reified T> CacheService.remember(key: String, expiresAt: LocalDateTime, callback: Supplier<T>): T? =
        remember(key, expiresAt, T::class.java, callback)


    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  callback
     * @return mixed
     */
    inline fun <reified T> CacheService.remember(key: String, callback: Supplier<T>): T? =
        remember(key, T::class.java, callback)


    /**
     * Retrieve an item from the cache.
     *
     * @param [key]
     * @param [default]
     * @return
     */
    inline fun <reified T> CacheService.get(key: String, default: T? = null): T? = get(key, T::class.java, default)
}
