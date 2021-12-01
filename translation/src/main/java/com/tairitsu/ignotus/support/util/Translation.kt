package com.tairitsu.ignotus.support.util

import com.tairitsu.ignotus.translation.TranslationBuilder
import com.tairitsu.ignotus.translation.TranslationService
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

object Translation {
    @JvmStatic
    lateinit var service: TranslationService

    @JvmStatic
    fun lang(key: String): String {
        @Suppress("UNCHECKED_CAST")
        return service.getMessage(key = key, args = Collections.EMPTY_MAP as Map<String, Any>, locale = LocaleContextHolder.getLocale(), default = "")
    }

    @JvmStatic
    fun lang(key: String, locale: Locale): String {
        @Suppress("UNCHECKED_CAST")
        return service.getMessage(key = key, args = Collections.EMPTY_MAP as Map<String, Any>, locale = locale, default = "")
    }

    @JvmStatic
    fun lang(key: String, args: Map<String, Any>): String {
        return service.getMessage(key = key, args = args, locale = LocaleContextHolder.getLocale(), default = "")
    }

    @JvmStatic
    fun lang(key: String, args: Map<String, Any>, locale: Locale): String {
        return service.getMessage(key = key, args = args, locale = locale, default = "")
    }

    @JvmStatic
    fun builder(): TranslationBuilder {
        return service.builder()
    }
}
