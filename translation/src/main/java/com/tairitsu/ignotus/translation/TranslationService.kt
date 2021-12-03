package com.tairitsu.ignotus.translation

import com.tairitsu.ignotus.translation.provider.MessageProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.stringtemplate.v4.ST
import java.util.*
import javax.annotation.PostConstruct

@Component
class TranslationService {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var application: ApplicationContext

    lateinit var providers: MutableMap<String, MessageProvider>

    @PostConstruct
    fun init() {
        providers = application.getBeansOfType(MessageProvider::class.java)
        com.tairitsu.ignotus.support.util.Translation.service = this
    }

    fun getTemplate(localeStr: String, key: String, args: Map<String, Any?>, locale: Locale): Pair<Boolean, String> {
        for (provider in providers.values) {
            val template = provider.getTemplate(localeStr, key, args, locale)
            if (template.first) {
                return template
            }
        }
        return false to ""
    }

    private fun getTemplate(locale: Locale, key: String, args: Map<String, Any?>): Pair<Boolean, String> {
        for (t in listOf(locale.toString().lowercase(),
            (locale.language + "_" + locale.country).lowercase(),
            locale.language.lowercase(),
            "en")) {
            val template = getTemplate(t, key, args, locale)
            if (template.first) {
                return template
            }
        }
        return false to ""
    }

    fun getMessage(key: String, args: Map<String, Any?>, locale: Locale, default: String): String {
        val template = getTemplate(locale, key, args)
        if (!template.first && default.isBlank()) {
            return ""
        }

        val message = if (template.first) template.second else default
        if (args.isEmpty()) {
            return message
        }

        val st = ST(message)
        for ((k, v) in args) {
            st.add(k, v)
        }
        return st.render(locale)
    }

    fun builder(): TranslationBuilder {
        return TranslationBuilder(this)
    }
}
