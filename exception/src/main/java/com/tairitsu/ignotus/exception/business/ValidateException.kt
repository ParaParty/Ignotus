package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：验证失败
 */
class ValidateException(detail: String, pointer: String) : SingleApiException(422, CODE, detail) {
    companion object {
        const val CODE = "validation_error"
    }

    init {
        this.title = translateTitle(CODE)

        val t = HashMap<String, String>()
        t["pointer"] = pointer
        source = t
    }
}
