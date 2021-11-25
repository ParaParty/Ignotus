package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：验证器错误
 */
class ValidationInvalidException(detail: String) : SingleApiException(404, "validation_invalid_exception", detail),
    LoggableException
