package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class TooManyRequestsException : SingleApiException {
    companion object {
        const val CODE = "too_many_requests"
    }

    constructor(detail: String) : super(429, CODE, detail)

    constructor() : super(429, CODE, Translation.translateDetail(CODE))

    init {
        this.title = Translation.translateTitle(CODE)
    }
}
