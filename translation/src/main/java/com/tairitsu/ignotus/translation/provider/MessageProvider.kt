package com.tairitsu.ignotus.translation.provider

import java.util.*

interface MessageProvider {
    fun getTemplate(localeStr: String, key: String, args: Map<String, Any?>, locale: Locale): Pair<Boolean, String>
}
