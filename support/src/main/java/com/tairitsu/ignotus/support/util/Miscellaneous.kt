package com.tairitsu.ignotus.support.util

import java.util.*

object UUIDUtils {
    @JvmStatic
    fun createModelId(): String = UUID.randomUUID().toString().replace("-", "")

    @JvmStatic
    fun createNoLeadingDigitId(): String {
        var ret = createModelId()
        while (ret[0] in '0'..'9') {
            ret = createModelId()
        }
        return ret
    }
}

operator fun String.times(times: Int): String {
    val builder = StringBuilder()
    for (i in 0 until times) {
        builder.append(this)
    }
    return builder.toString()
}

operator fun Char.times(times: Int): String {
    val builder = StringBuilder()
    for (i in 0 until times) {
        builder.append(this)
    }
    return builder.toString()
}

