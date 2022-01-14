package com.tairitsu.ignotus.cache.filesystem

import com.tairitsu.ignotus.cache.CacheConfig
import com.tairitsu.ignotus.cache.CacheService
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import kotlin.concurrent.withLock


class FilesystemCacheService(private val config: CacheConfig) : CacheService {

    val lock = ReentrantLock()
    val lockMap = ConcurrentHashMap<String, String>()

    companion object {
        private val LOCK_CAPACITY = Duration.ofSeconds(30).toMillis()
    }

    private fun setLock(key: String): Boolean {
        val now: Long = Date().time
        val nowStr = now.toString()
        return lock.withLock t@{
            val lastLockStr = lockMap[key]

            if (lastLockStr == null) {
                lockMap[key] = nowStr
                return@t true
            }

            val lastLock = try {
                lastLockStr.toLong()
            } catch (ignored: Exception) {
                return@t false
            }

            if (lastLock + LOCK_CAPACITY < now) {
                lockMap[key] = nowStr
                return@t true
            }
            return@t false
        }
    }

    private fun releaseLock(key: String): Boolean {
        return lock.withLock t@{
            val lastLockStr = lockMap[key] ?: return@t false
            val lastLock = try {
                lastLockStr.toLong()
            } catch (ignored: Exception) {
                return@t false
            }

            lockMap.remove(key)
            return@t true
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
        TODO("Not yet implemented")
    }

    /**
     * Store an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return bool
     */
    override fun <T> put(key: String, value: T): Boolean {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    /**
     * Increment the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun increment(key: String, value: Long): Long? {
        TODO("Not yet implemented")
    }

    /**
     * Decrement the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun decrement(key: String, value: Long): Long? {
        TODO("Not yet implemented")
    }

    /**
     * Store an item in the cache indefinitely.
     *
     * @param  key
     * @param  value
     * @return bool
     */
    override fun <T> forever(key: String, value: T): Boolean {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  callback
     * @param  type
     * @return mixed
     */
    override fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    /**
     * Get an item from the cache, or execute the given Closure and store the result forever.
     *
     * @param  key
     * @param  callback
     * @param  type
     * @return mixed
     */
    override fun <T> rememberForever(key: String, type: Class<T>, callback: Supplier<T>): T? {
        TODO("Not yet implemented")
    }

    /**
     * Remove an item from the cache.
     *
     * @param  key
     * @return bool
     */
    override fun forget(key: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Check if a key exists.
     *
     * @param  key
     * @return bool
     */
    override fun has(key: String): Boolean {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

}
