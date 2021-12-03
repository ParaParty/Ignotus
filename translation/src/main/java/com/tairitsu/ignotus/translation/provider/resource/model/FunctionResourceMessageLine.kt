package com.tairitsu.ignotus.translation.provider.resource.model

import java.util.*

/**
 * 回调函数形式的文本字段
 */
class FunctionResourceMessageLine(
    private val value: (String, Map<String, Any?>, Locale) -> String,
) : ResourceMessageLine {
    override fun get(key: String, args: Map<String, Any?>, locale: Locale) = value(key, args, locale)
}
