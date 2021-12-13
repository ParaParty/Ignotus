package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：验证失败
 */
class ValidateException : SingleApiException {
    constructor(detail: String, pointer: String) : super(422, "validation_error", detail) {
        val t = HashMap<String, String>();
        t["pointer"] = pointer

        source = t
    }
}
