package com.tairitsu.ignotus.translation.provider.resource.model

import java.util.*

/**
 * 字符串形式的文本字段
 */
class StringResourceMessageLine(private val value: String) : ResourceMessageLine {
    override fun get(key: String, args: Map<String, Any?>, locale: Locale) = value
}

