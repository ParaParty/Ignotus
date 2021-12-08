package com.tairitsu.ignotus.foundation.model.dto

import com.tairitsu.ignotus.validation.annotation.Required

class JsonApiObject<T> {
    var id: String? = null

    var type: String? = null

    @Required
    var attributes: T? = null
}

class JsonApiObjectBody<T> {
    @Required
    var data: JsonApiObject<T>? = null
}

class JsonApiListBody<T> {
    @Required
    var data: List<JsonApiObject<T>>? = null
}
