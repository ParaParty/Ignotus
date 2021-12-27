package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：未登录
 */
class UnauthorizedException : SingleApiException {
    companion object {
        const val CODE = "unauthorized"
    }

    constructor(detail: String) : super(401, CODE, detail)

    constructor() : super(401, CODE, translateDetail(CODE))

    init {
        this.title = translateTitle(CODE)
    }
}
