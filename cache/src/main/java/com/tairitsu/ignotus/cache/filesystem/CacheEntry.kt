package com.tairitsu.ignotus.cache.filesystem

class CacheEntry {
    var id: String = ""

    var validUntilMillis: Long = 0

    var validUntil: String = ""

    var value: String = ""
}

class CacheSet : HashMap<String, CacheEntry>()
