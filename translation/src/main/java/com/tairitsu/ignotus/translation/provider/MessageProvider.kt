package com.tairitsu.ignotus.translation.provider

interface MessageProvider {
    fun getTemplate(locale: String, key: String): Pair<Boolean, String>
}
