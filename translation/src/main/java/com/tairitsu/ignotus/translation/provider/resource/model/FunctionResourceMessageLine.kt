package com.tairitsu.ignotus.translation.provider.resource.model

import com.tairitsu.ignotus.translation.provider.resource.dsl.FunctionMessageLineArgs
import java.util.*

/**
 * 回调函数形式的文本字段
 */
class FunctionResourceMessageLine(
    private val value: (FunctionMessageLineArgs) -> String,
) : ResourceMessageLine {
    override fun get(key: String, args: Map<String, Any?>, locale: Locale): String {
        val data = FunctionMessageLineArgs(key, args, locale)
        return value(data)
    }
}
