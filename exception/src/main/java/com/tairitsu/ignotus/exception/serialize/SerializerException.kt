package com.tairitsu.ignotus.exception.serialize

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 序列化异常
 */
class SerializerException(reason: Reason) : SingleApiException(500, CODE, ""), LoggableException {
    companion object {
        private const val CODE = "serializer_exception"
    }

    enum class Reason(val s: String) {
        API_RESULT_UNACCEPTABLE_TYPE("api_result_unacceptable_type"),
        RELATED_OBJECT_UNACCEPTABLE_TYPE("related_object_unacceptable_type")
    }

    init {
        this.title = translateTitle(CODE)
        this.detail = when (reason) {
            Reason.API_RESULT_UNACCEPTABLE_TYPE -> translateDetail(CODE, reason.s)
            Reason.RELATED_OBJECT_UNACCEPTABLE_TYPE -> translateDetail(CODE, reason.s)
        }
    }
}
