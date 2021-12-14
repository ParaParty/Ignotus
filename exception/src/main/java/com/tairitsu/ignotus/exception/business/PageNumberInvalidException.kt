package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException
import java.util.*

/**
 * 业务异常：页码格式错误
 */
class PageNumberInvalidException(parameterName: String, reason: Reason, pageStartsAt: Int = -1) :
    SingleApiException(400, CODE, "") {

    enum class Reason(val s: String) {
        FAIL_TO_PARSE("fail_to_parse"),
        OUT_OF_RANGE("out_of_range"),
    }

    companion object {
        const val CODE = "page_number_invalid"
    }

    init {
        detail = when (reason) {
            Reason.FAIL_TO_PARSE -> translateDetail(CODE, "fail_to_parse")
            Reason.OUT_OF_RANGE -> translateDetail(CODE, "out_of_range", Collections.singletonMap("min", pageStartsAt))
        }

        this.title = translateTitle(CODE)

        val t = HashMap<String, String>()
        this.source = t
        t["parameter"] = parameterName
    }
}
