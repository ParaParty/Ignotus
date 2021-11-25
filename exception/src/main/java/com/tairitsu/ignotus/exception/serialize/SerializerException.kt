package com.tairitsu.ignotus.exception.serialize

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 序列化异常
 */
class SerializerException : SingleApiException, LoggableException {
    constructor(detail: String) : super(500, "serializer_exception", detail)
}
