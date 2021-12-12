package com.tairitsu.ignotus.cache.mongo

import com.mongodb.client.result.DeleteResult
import com.tairitsu.ignotus.cache.CacheService
import com.tairitsu.ignotus.support.util.Base64Utils.base64Encode
import com.tairitsu.ignotus.support.util.JSON.jsonToObject
import com.tairitsu.ignotus.support.util.JSON.toJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier


class MongoCacheService(private val mongoTemplate: MongoTemplate) : CacheService {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val LOCK_PREFIX = "_LOCK:"
        private val LOCK_CAPACITY = Duration.ofSeconds(30).toMillis()
    }

    private fun setLock(key: String, token: String, retry: Boolean = true): String? {
        val lockKey = (LOCK_PREFIX + key).base64Encode()
        val now = System.currentTimeMillis()
        val validUntil = now + LOCK_CAPACITY
        val validUntilDate = Date(validUntil).toString()

        val query = Query.query(Criteria.where("_id").`is`(lockKey))
        val update: Update = Update()
            .setOnInsert("_id", lockKey)
            .setOnInsert("valid_until", validUntil)
            .setOnInsert("valid_until_friendly", validUntilDate)
            .setOnInsert("token", token)

        val options = FindAndModifyOptions().upsert(true).returnNew(true)
        val doc = mongoTemplate.findAndModify(query, update, options, CacheEntry::class.java)

        val locked = doc!!.token == token

        if (!locked && doc.validUntilMillis < now) {
            val deleteQuery = Query.query(Criteria.where("_id").`is`(lockKey)
                .and("token").`is`(doc.token)
                .and("valid_until").`is`(doc.validUntilMillis))
            val deleted: DeleteResult = mongoTemplate.remove(deleteQuery, CacheEntry::class.java)
            if (deleted.deletedCount >= 1 && retry) {
                return this.setLock(key, token, false)
            }
        }

        return if (locked) token else null
    }

    private fun releaseLock(key: String, token: String): Boolean {
        val lockKey = (LOCK_PREFIX + key).base64Encode()

        val query = Query.query(Criteria.where("_id").`is`(lockKey)
            .and("token").`is`(token))
        val deleted: DeleteResult = mongoTemplate.remove(query, CacheEntry::class.java)
        val released = deleted.deletedCount == 1L
        if (released) {
            log.debug("Remove query successfully affected 1 record for key {} with token {}",
                key, token)
        } else if (deleted.deletedCount > 0) {
            log.error("Unexpected result from release for key {} with token {}, released {}",
                key, token, deleted)
        } else {
            log.error("Remove query did not affect any records for key {} with token {}",
                key, token)
        }

        return released
    }

    private inline fun lock(key: String, block: () -> Unit) {
        val lockToken = UUID.randomUUID().toString()
        while (true) {
            if (setLock(key, lockToken) != null) {
                block()
                releaseLock(key, lockToken)
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
            val query = Query(Criteria.where("_id").`is`(key.base64Encode()))
            val cache = mongoTemplate.findOne(query, CacheEntry::class.java)
            if (cache != null) {
                val value = cache.value
                ret = value.jsonToObject(type)
            }
            mongoTemplate.remove(query, CacheEntry::class.java)
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
        val entry = CacheEntry()
        entry.id = key.base64Encode()
        entry.value = value?.toJson() ?: "null"

        mongoTemplate.save(entry)
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
        val now = System.currentTimeMillis()
        val validUntil = now + ttl * 1000
        val validUntilDate = Date(validUntil).toString()

        val entry = CacheEntry()
        entry.id = key.base64Encode()
        entry.value = value?.toJson() ?: "null"
        entry.validUntilMillis = validUntil
        entry.validUntil = validUntilDate

        mongoTemplate.save(entry)
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
        var ret = false
        lock(key) {
            if (!has(key)) {
                put(key, value, ttl)
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
            if (!has(key)) {
                put(key, value, ttl)
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
        var ret = false
        lock(key) {
            if (!has(key)) {
                put(key, value, expiresAt)
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
        throw UnsupportedOperationException("increment() is not supported")
    }

    /**
     * Decrement the value of an item in the cache.
     *
     * @param  [key]
     * @param  [value]
     * @return Long
     */
    override fun decrement(key: String, value: Long): Long? {
        throw UnsupportedOperationException("decrement() is not supported")
    }

    /**
     * Store an item in the cache indefinitely.
     *
     * @param  key
     * @param  value
     * @return bool
     */
    override fun <T> forever(key: String, value: T): Boolean {
        return put(key, value)
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


    private fun <T> rememberOptional(key: String, ttl: Duration? = null, type: Class<T>, callback: Supplier<T>): T? {
        var ret: T? = null
        lock(key) {
            if (has(key)) {
                ret = get(key, type)
            }

            if (ret == null) {
                val value = callback.get()

                val result = if (ttl == null) {
                    put(key, value as Any)
                } else {
                    put(key, value as Any, ttl)
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
     * @param  type
     * @param  callback
     * @return mixed
     */
    override fun <T> remember(key: String, type: Class<T>, callback: Supplier<T>): T? {
        return rememberOptional(key, null, type, callback)
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
        val query = Query(Criteria.where("_id").`is`(key.base64Encode()))
        val result = mongoTemplate.remove(query, CacheEntry::class.java)
        return result.deletedCount > 0
    }

    /**
     * Check if a key exists.
     *
     * @param  key
     * @return bool
     */
    override fun has(key: String): Boolean {
        val query = Query(Criteria.where("_id").`is`(key.base64Encode()))
        val cache = mongoTemplate.findOne(query, CacheEntry::class.java) ?: return false
        if (cache.validUntilMillis < System.currentTimeMillis()) {
            return false
        }
        return true
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
        val query = Query(Criteria.where("_id").`is`(key.base64Encode()))
        val cache = mongoTemplate.findOne(query, CacheEntry::class.java) ?: return null
        if (cache.validUntilMillis < System.currentTimeMillis()) {
            return null
        }
        val value = cache.value
        return value.jsonToObject(type)
    }
}

