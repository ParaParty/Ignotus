package com.tairitsu.ignotus.foundation.model

import com.tairitsu.ignotus.validation.annotation.Required

/**
 * 一个 Json Api 格式的对象
 */
class JsonApiObject<T> {
    var type: String? = null

    var id: String? = null

    var meta: Map<String, Any?>? = null

    @Required
    var attributes: T? = null
}

/**
 * 一个由单个 Json Api 对象组成的请求体
 */
class JsonApiObjectBody<T> {
    @Required
    @field:JvmSynthetic
    lateinit var data: JsonApiObject<T>
}

/**
 * 一个由多个 Json Api 对象组成的请求体
 */
class JsonApiListBody<T> {
    @Required
    @field:JvmSynthetic
    lateinit var data: List<JsonApiObject<T>>
}
