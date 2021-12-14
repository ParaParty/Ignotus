package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException
import java.util.HashMap

/**
 * 业务异常：页偏移格式错误
 */
class PageOffsetInvalidException(parameterName: String, reason: Reason) : SingleApiException(400, CODE, "") {
    companion object {
        const val CODE = "page_offset_invalid"
    }

    enum class Reason(val s: String) {
        FAIL_TO_PARSE("fail_to_parse"),
        OUT_OF_RANGE("out_of_range"),
    }


    init {
        this.title = translateTitle(CODE)

        this.detail = translateDetail(CODE, reason.s)

        val t = HashMap<String, String>()
        this.source = t
        t["parameter"] = parameterName
    }
}
