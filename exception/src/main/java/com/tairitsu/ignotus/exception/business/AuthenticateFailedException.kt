package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：登录失败或权限不足
 */
class AuthenticateFailedException : SingleApiException {
    constructor(detail: String) : super(403, "authenticate_failed", detail)

    constructor() : super(403, "authenticate_failed", "")
}
