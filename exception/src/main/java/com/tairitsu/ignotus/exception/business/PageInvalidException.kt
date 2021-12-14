package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页码无效
 */
class PageInvalidException(reason: Reason) : SingleApiException(500,
    CODE,
    translateDetail(CODE, reason.s)) {

    companion object {
        const val CODE = "page_invalid"
    }

    enum class Reason(val s: String) {
        START_AT_ERROR("start_at_error")
    }

    init {
        this.title = translateTitle(CODE)
    }
}
