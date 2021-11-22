package com.tairitsu.ignotus.support.util

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

object JSON {
    private val mapper = JsonMapper.builder()
        .addModule(ParameterNamesModule())
        .addModule(Jdk8Module())
        .addModule(JavaTimeModule())
        .addModule(KotlinModule.Builder().build())
        .build()

    @JvmStatic
    fun <T> parseObject(content: String, clazz: Class<T>): T? {
        return mapper.readValue(content, clazz)
    }

    fun <T> String.jsonToObject(clazz: Class<T>): T? {
        return try {
            mapper.readValue(this, clazz)
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

