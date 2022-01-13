package com.tairitsu.ignotus.cache

import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

internal abstract class AutoConfigurationCondition : Condition {

    abstract val type: List<String>

    final override fun matches(context: ConditionContext, metadata: AnnotatedTypeMetadata): Boolean {
        val app = context.beanFactory!!
        val config = app.getBean(CacheConfig::class.java).autoConfiguration

        val setType = config.type.lowercase()
        return config.enabled && (setType == "auto" || setType in type)
    }
}
