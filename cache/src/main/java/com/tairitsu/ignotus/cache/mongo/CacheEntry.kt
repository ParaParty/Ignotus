package com.tairitsu.ignotus.cache.mongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "_CACHE")
class CacheEntry {
    @Id
    var id: String = ""

    @Field("id_friendly")
    var idFriendly: String = ""

    @Field("valid_until")
    var validUntilMillis: Long = 0

    @Field("valid_until_friendly")
    var validUntil: String = ""

    @Field("value")
    var value: String = ""

    @Field("token")
    var token: String = ""
}
