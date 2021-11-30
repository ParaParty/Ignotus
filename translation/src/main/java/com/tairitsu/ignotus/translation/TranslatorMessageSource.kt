//package com.tairitsu.ignotus.translation.service
//
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.context.HierarchicalMessageSource
//import org.springframework.context.MessageSource
//import org.springframework.context.MessageSourceResolvable
//import org.springframework.context.support.MessageSourceSupport
//import org.springframework.stereotype.Component
//import java.util.*
//
//@Component
//class TranslatorMessageSource(private val messageSource: MessageSource)  : MessageSourceSupport(), HierarchicalMessageSource {
//    private val log: Logger = LoggerFactory.getLogger(this::class.java)
//
//    private var parentMessageSource: MessageSource? = null
//
//    init {
//        if (messageSource is HierarchicalMessageSource) {
//            parentMessageSource = messageSource.parentMessageSource
//            messageSource.parentMessageSource = this
//        } else {
//            log.warn("Can not set Ignotus Message Source to DelegatingMessageSource")
//        }
//    }
//
//    override fun setParentMessageSource(parent: MessageSource?) {
//        this.parentMessageSource = parent
//    }
//
//    override fun getParentMessageSource(): MessageSource? {
//        return this.parentMessageSource
//    }
//
//    override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String? {
//        return parentMessageSource?.getMessage(code, args, defaultMessage, locale)
//    }
//
//    override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
//        return parentMessageSource?.getMessage(code, args, locale) ?: ""
//    }
//
//    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
//        return parentMessageSource?.getMessage(resolvable, locale) ?: ""
//    }
//}
