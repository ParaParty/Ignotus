package com.tairitsu.ignotus.support.util

import com.fasterxml.jackson.core.type.TypeReference
import com.tairitsu.ignotus.support.service.JSONMapperRegister

object JSON {
    private val mapper
        get() = JSONMapperRegister.objectMapperProvider.objectMapper

    @JvmStatic
    fun <T> parseObject(content: String, clazz: Class<T>): T? {
        return mapper.readValue(content, clazz)
    }

    @JvmStatic
    fun <T> parseObject(content: String, type: TypeReference<T>): T? {
        return mapper.readValue(content, type)
    }

    @JvmStatic
    fun parseObjectToMap(content: String): Map<String, Any> {
        return mapper.readValue(content, object : TypeReference<Map<String, Any>>() {})
    }

    fun <T> String.jsonToObject(clazz: Class<T>): T? {
        return try {
            mapper.readValue(this, clazz)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> String.jsonToObject(type: TypeReference<T>): T? {
        return try {
            mapper.readValue(this, type)
        } catch (e: Exception) {
            null
        }
    }

    fun String.jsonToMap(): Map<String, Any>? {
        return try {
            mapper.readValue(this, object : TypeReference<Map<String, Any>>() {})
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun <T> toJSONString(value: T): String {
        return mapper.writeValueAsString(value)
    }

    fun Any.toJson(): String {
        return mapper.writeValueAsString(this)
    }
}

