package com.tairitsu.ignotus.exception

import java.util.*

/**
 * 单条错误消息同时返回给前端时用的异常类
 */
open class SingleApiException : ApiException {
    var status: Int protected set
    var code: String protected set
    var detail: String protected set

    constructor(status: Int, code: String, detail: String) : super(detail) {
        this.status = status
        this.code = code
        this.detail = detail
    }

    /**
     * 序列化为错误信息
     */
    open fun toJSONObject(): HashMap<String, Any> {
        val e = HashMap<String, Any>()
        e["status"] = status.toString()
        e["code"] = code
        e["detail"] = detail
        return e
    }

    override fun toJSONArray(): Iterable<Any> {
        return Collections.singletonList(toJSONObject())
    }
}
