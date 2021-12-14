package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：验证器错误
 */
class ValidationInvalidException(detail: String) : SingleApiException(404, CODE, detail),
    LoggableException {
    companion object {
        const val CODE = "validation_invalid_exception"
    }
}
