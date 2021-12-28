package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：权限不足
 */
class AuthenticateFailedException : SingleApiException {
    companion object {
        const val CODE = "authenticate_failed"
    }

    constructor(detail: String) : super(403, CODE, detail)

    constructor() : super(403, CODE, Translation.translateDetail(CODE))

    init {
        this.title = Translation.translateTitle(CODE)
    }
}

