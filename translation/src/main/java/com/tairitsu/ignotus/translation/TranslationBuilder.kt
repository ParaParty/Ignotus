package com.tairitsu.ignotus.translation

import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

class TranslationBuilder(private val translationService: TranslationService) {
    private lateinit var key: String

    private var args: HashMap<String, Any?> = HashMap()

    private var locale: Locale = LocaleContextHolder.getLocale()

    private var default: String = ""

    fun build(): String {
        return translationService.getMessage(key = key, args = args, locale = locale, default = default)
    }

    fun setKey(key: String): TranslationBuilder {
        this.key = key
        return this
    }

    fun setLocale(locale: Locale): TranslationBuilder {
        this.locale = locale
        return this
    }

    fun setDefault(default: String): TranslationBuilder {
        this.default = default
        return this
    }

    fun add(key: String, value: Any?): TranslationBuilder {
        args[key] = value
        return this
    }
}
