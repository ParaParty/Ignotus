package com.tairitsu.ignotus.cache.filesystem

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.tairitsu.ignotus.cache.CacheService
import com.tairitsu.ignotus.support.util.Base64Utils.base64Encode
import com.tairitsu.ignotus.support.util.JSON.jsonToObject
import com.tairitsu.ignotus.support.util.JSON.toJson
import org.springframework.util.DigestUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier
import kotlin.concurrent.withLock


class FilesystemCacheService(storagePath: String) : CacheService {

    private val lock = ReentrantLock()
    private val lockMap = ConcurrentHashMap<String, String>()

    private val objectMapper = JsonMapper.builder()
        .addModule(JavaTimeModule())
        .addModule(KotlinModule.Builder().build())
        .build()

    init {
        objectMapper.propertyNamingStrategy = PropertyNamingStrategies.SnakeCaseStrategy()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val baseStoragePath = File(storagePath)

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
            @Suppress("UNUSED_VARIABLE") val lastLock = try {
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

    @Suppress("DuplicatedCode")
    private fun getValue(key: String): CacheEntry? {
        val hash = DigestUtils.md5DigestAsHex(key.toByteArray())
        val path = hash.subSequence(0, 2).toString()
        val file = File(File(baseStoragePath, path), "$hash.json")

        if (!file.exists()) {
            return null
        }

        val buffer = file.bufferedReader()
        val data = objectMapper.readValue(buffer, CacheSet::class.java)

        val record = data[key] ?: return null
        val now = System.currentTimeMillis()

        return if (record.validUntilMillis == 0L || now < record.validUntilMillis) {
            record
        } else {
            null
        }
    }

    private fun hasValue(key: String) = getValue(key) != null

    @Suppress("DuplicatedCode")
    private fun setValue(record: CacheEntry): Boolean {
        val hash = DigestUtils.md5DigestAsHex(record.id.toByteArray())
        val path = hash.subSequence(0, 2).toString()
        val folder = File(baseStoragePath, path)
        val file = File(folder, "$hash.json")

        val buffer = if (file.exists()) {
            file.bufferedReader()
        } else {
            null
        }

        val data = if (buffer == null) {
            CacheSet()
        } else {
            objectMapper.readValue(buffer, CacheSet::class.java)
        }

        data[record.id] = record


        Files.createDirectories(Paths.get(folder.toURI()))
        objectMapper.writeValue(file.writer(), data)
        return true
    }

    @Suppress("DuplicatedCode")
    private fun delValue(key: String): CacheEntry? {
        val hash = DigestUtils.md5DigestAsHex(key.toByteArray())
        val path = hash.subSequence(0, 2).toString()
        val file = File(File(baseStoragePath, path), "$hash.json")

        val buffer = if (file.exists()) {
            file.bufferedReader()
        } else {
            null
        }

        val data = if (buffer == null) {
            CacheSet()
        } else {
            objectMapper.readValue(buffer, CacheSet::class.java)
        }

        val ret = data[key]
        data.remove(key)

        objectMapper.writeValue(file.writer(), data)
        return ret
    }

    @Suppress("DuplicatedCode")
    private  fun <T> putValue(key: String, value: T, ttl: Long): Boolean {
        val now = System.currentTimeMillis()
        val validUntil = now + ttl * 1000
        val validUntilDate = Date(validUntil).toString()

        val record = CacheEntry()
        record.id = key.base64Encode()
        record.value = value?.toJson() ?: "null"
        record.validUntilMillis = validUntil
        record.validUntil = validUntilDate

        setValue(record)
        return true
    }

    @Suppress("DuplicatedCode")
    private  fun <T> putValueForever(key: String, value: T): Boolean {
        val record = CacheEntry()
        record.id = key
        record.value = value?.toJson() ?: "null"
        setValue(record)
        return true
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
            val record = getValue(key)
            if (record != null) {
                delValue(key)
                ret = record.value.jsonToObject(type)
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
        lock(key) {
            putValueForever(key, value)
        }
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
        lock(key) {
            putValue(key, value, ttl)
        }
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
        return put(key, value as Any, ttl.seconds)
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
        return put(key, value, duration.seconds)
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
        val ret = false
        lock(key) {
            if (!hasValue(key)) {
                putValue(key, value, ttl)
                return true
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
        return add(key, value, ttl.seconds)
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
        return add(key, value, duration.seconds)
    }

    /**
     * Increment the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun increment(key: String, value: Long): Long? {
        var ret: Long? = null

        lock(key) {
            val record = getValue(key) ?: CacheEntry().also { s ->
                s.id = key
                s.value = "0"
            }

            val newValue = try {
                record.value.toLong() + value
            } catch (ignored: Exception) {
                null
            }

            if (newValue != null) {
                record.value = "" + newValue
                ret = newValue
            }
        }

        return ret
    }

    /**
     * Decrement the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun decrement(key: String, value: Long): Long? {
        var ret: Long? = null

        lock(key) {
            val record = getValue(key) ?: CacheEntry().also { s ->
                s.id = key
                s.value = "0"
            }

            val newValue = try {
                record.value.toLong() - value
            } catch (ignored: Exception) {
                null
            }

            if (newValue != null) {
                record.value = "" + newValue
                ret = newValue
            }
        }

        return ret
    }

    private fun <T> rememberOptional(key: String, ttl: Duration? = null, type: Class<T>, callback: Supplier<T>): T? {
        var ret: T? = null
        lock(key) {
            if (hasValue(key)) {
                ret = getValue(key)?.value?.jsonToObject(type)
            }

            if (ret == null) {
                val value = callback.get()

                val result = if (ttl == null) {
                    putValueForever(key, value as Any)
                } else {
                    putValue(key, value as Any, ttl.seconds)
                }

                ret = if (result) value else null
            }
        }
        return ret
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
        return rememberOptional(key, ttl, type, callback)
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
     * @param  callback
     * @param  type
     * @return mixed
     */
    override fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T? {
        return rememberOptional(key, null, type, callback)
    }

    /**
     * Remove an item from the cache.
     *
     * @param  key
     * @return bool
     */
    override fun forget(key: String): Boolean {
        lock(key) {
            delValue(key)
        }
        return true
    }

    /**
     * Check if a key exists.
     *
     * @param  key
     * @return bool
     */
    override fun has(key: String): Boolean {
        var ret = false
        lock(key) {
            ret = hasValue(key)
        }
        return ret
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
        var ret: T? = default
        lock(key) {
            ret = getValue(key)?.value?.jsonToObject(type)
        }
        return ret
    }
}
