package com.tairitsu.ignotus.support.util

import java.util.*

object UUIDUtils {
    /**
     * 创建一个去除横杠的 UUID。
     */
    @JvmStatic
    fun createModelId(): String = UUID.randomUUID().toString().replace("-", "")

    /**
     * 创建一个起始字符不为数字的去横杠的 UUID。
     */
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

