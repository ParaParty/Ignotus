package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：找不到资源
 */
class ModelNotFoundException : SingleApiException  {
    constructor(detail: String) : super(404, "model_not_found", detail)

    constructor() : super(404, "model_not_found", "")
}

