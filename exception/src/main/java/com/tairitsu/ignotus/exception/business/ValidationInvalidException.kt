package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

class ValidationInvalidException(detail: String) : SingleApiException(404, "validation_invalid_exception", detail),
    LoggableException
