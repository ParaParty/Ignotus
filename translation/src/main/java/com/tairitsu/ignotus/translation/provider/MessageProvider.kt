package com.tairitsu.ignotus.translation.provider

import java.util.*

interface MessageProvider {
    /**
     * 优先度，数值越大，优先级越高
     */
    val priority: Int

    fun getTemplate(localeStr: String, key: String, args: Map<String, Any?>, locale: Locale): Pair<Boolean, String>
}
