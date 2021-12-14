package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：找不到资源
 */
class ModelNotFoundException : SingleApiException  {
    companion object {
        const val CODE = "model_not_found"
    }

    constructor(detail: String) : super(404, CODE, detail)

    constructor() : super(404, CODE, translateDetail(CODE))

    init {
        this.title = translateTitle(CODE)
    }
}

