package com.tairitsu.ignotus.translation

import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

/**
 * 文本字段构造器
 */
class TranslationBuilder(private val translationService: TranslationService) {
    private lateinit var key: String

    private var args: HashMap<String, Any?> = HashMap()

    private var locale: Locale = LocaleContextHolder.getLocale()

    private var default: String = ""

    /**
     * 构造文本字段
     */
    fun build(): String {
        return translationService.getMessage(key = key, args = args, locale = locale, default = default)
    }

    /**
     * 设置文本字段名
     */
    fun setKey(key: String): TranslationBuilder {
        this.key = key
        return this
    }

    /**
     * 设置语言环境
     */
    fun setLocale(locale: Locale): TranslationBuilder {
        this.locale = locale
        return this
    }

    /**
     * 设置默认值
     */
    fun setDefault(default: String): TranslationBuilder {
        this.default = default
        return this
    }

    /**
     * 添加文本参数
     */
    fun add(key: String, value: Any?): TranslationBuilder {
        args[key] = value
        return this
    }
}
