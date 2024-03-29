package com.tairitsu.ignotus.support.util

import com.tairitsu.ignotus.translation.TranslationBuilder
import com.tairitsu.ignotus.translation.TranslationService
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

/**
 * 文本字段模块
 */
class Translation private constructor(baseKey: String = "") {
    companion object {
        /**
         * 文本字段服务（需待该服务初始化完毕后本模块才能使用）
         */
        @JvmStatic
        lateinit var service: TranslationService

        private val instance: Translation = Translation()

        /**
         * 按照指定名字获取文本字段
         * 语言环境：使用当前请求或默认语言环境
         * 默认值：空字符串
         *
         * @param key 文本字段名
         */
        @JvmStatic
        fun lang(key: String): String {
            @Suppress("UNCHECKED_CAST")
            return instance.getMessage(key = key,
                args = Collections.EMPTY_MAP as Map<String, Any>,
                locale = LocaleContextHolder.getLocale(),
            )
        }

        /**
         * 按照指定名字获取文本字段
         * 默认值：空字符串
         *
         * @param key 文本字段名
         * @param locale 语言环境
         */
        @JvmStatic
        fun lang(key: String, locale: Locale): String {
            @Suppress("UNCHECKED_CAST")
            return instance.getMessage(key = key,
                args = Collections.EMPTY_MAP as Map<String, Any>,
                locale = locale,
            )
        }

        /**
         * 按照指定名字获取文本字段
         * 语言环境：使用当前请求或默认语言环境
         * 默认值：空字符串
         *
         * @param key 文本字段名
         * @param args 文本参数
         */
        @JvmStatic
        fun lang(key: String, args: Map<String, Any>): String {
            return instance.getMessage(key = key, args = args, locale = LocaleContextHolder.getLocale())
        }

        /**
         * 按照指定名字获取文本字段
         * 默认值：空字符串
         *
         * @param key 文本字段名
         * @param locale 语言环境
         * @param args 文本参数
         */
        @JvmStatic
        fun lang(key: String, args: Map<String, Any>, locale: Locale): String {
            return instance.getMessage(key = key, args = args, locale = locale)
        }

        /**
         * 获取文本字段构造器
         */
        @JvmStatic
        fun builder(): TranslationBuilder {
            return service.builder()
        }

        /**
         * 获取文本字段构造实例
         */
        @JvmStatic
        fun getTranslationService(baseKey: String) = Translation(baseKey)
    }

    val baseKey: String

    init {
        this.baseKey = if (baseKey.isBlank()) {
            ""
        } else if (baseKey.endsWith(".")) {
            baseKey
        } else {
            "$baseKey."
        }
    }

    /**
     * 按照指定名字获取文本字段
     * 默认值：空字符串
     *
     * @param key 文本字段名
     * @param locale 语言环境
     * @param args 文本参数
     * @param default 默认值
     */
    fun getMessage(
        key: String,
        @Suppress("UNCHECKED_CAST") args: Map<String, Any?> = Collections.EMPTY_MAP as Map<String, Any>,
        locale: Locale = LocaleContextHolder.getLocale(),
        default: String = "$baseKey$key",
    ): String {
        return service.getMessage(key = baseKey + key, args = args, locale = locale, default = default)
    }
}
