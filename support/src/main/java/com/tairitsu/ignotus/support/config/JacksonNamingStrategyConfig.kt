package com.tairitsu.ignotus.support.config

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils
import javax.annotation.PostConstruct

@Component
class JacksonNamingStrategyConfig {
    @Autowired
    lateinit var appContext: ApplicationContext

    @PostConstruct
    fun init() {
        val t = appContext.getBean(JacksonProperties::class.java)
        val strategy = t.propertyNamingStrategy ?: return
        val propertyNamingStrategyClass = ClassUtils.forName(strategy, null)
        NAMING_BASE = BeanUtils.instantiateClass(propertyNamingStrategyClass) as PropertyNamingStrategy?
    }

    companion object {
        private var NAMING_BASE: PropertyNamingStrategy? = null
        val namingStrategy: PropertyNamingStrategy? get() = NAMING_BASE
    }
}
