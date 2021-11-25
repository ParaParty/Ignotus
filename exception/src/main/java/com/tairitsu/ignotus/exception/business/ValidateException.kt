package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：验证失败
 */
class ValidateException : SingleApiException {

    private val source = mutableMapOf<String, String>()

    constructor(detail: String, pointer: String) : super(422, "validation_error", detail) {
        source["pointer"] = pointer
    }

    override fun toJSONObject(): HashMap<String, Any> {
        val e = super.toJSONObject()
        e["source"] = source
        return e
    }
}
