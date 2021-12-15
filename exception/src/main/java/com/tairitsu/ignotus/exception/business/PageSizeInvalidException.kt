package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页大小格式错误
 */
class PageSizeInvalidException(parameterName: String, reason: Reason, minLimit: Int = 0, maxLimit: Int = 0) : SingleApiException(404, CODE, "") {
    companion object {
        const val CODE = "page_size_invalid"
    }

    enum class Reason(val s: String) {
        FAIL_TO_PARSE("fail_to_parse"),
        OUT_OF_RANGE("out_of_range"),
    }

    init {
        this.title = translateTitle(CODE)

        this.detail = when (reason) {
            Reason.FAIL_TO_PARSE -> translateDetail(CODE, reason.s)
            Reason.OUT_OF_RANGE -> translateDetail(CODE, reason.s, mapOf("min" to minLimit, "max" to maxLimit))
        }

        val t = HashMap<String, String>()
        this.source = t
        t["parameter"] = parameterName
    }
}
