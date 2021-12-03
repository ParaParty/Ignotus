package com.tairitsu.ignotus.translation.provider.resource.model

import java.util.*

/**
 * 文本字段
 */
interface ResourceMessageLine {
    fun get(key: String, args: Map<String, Any?>, locale: Locale): String
}
