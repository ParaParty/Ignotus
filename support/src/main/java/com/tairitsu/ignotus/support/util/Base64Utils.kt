package com.tairitsu.ignotus.support.util

import java.util.*

object Base64Utils {

    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()

    fun Base64.Encoder.encode(s: String) = this.encodeToString(s.toByteArray())

    fun String.base64Encode() = base64Encoder.encodeToString(this.toByteArray())

    fun String.base64DecodeToString() = base64Decoder.decode(this).toString(Charsets.UTF_8)

    @JvmStatic
    fun encode(s: String) = base64Encoder.encodeToString(s.toByteArray())

    @JvmStatic
    fun decodeToString(s: String) = base64Decoder.decode(s).toString(Charsets.UTF_8)
}
