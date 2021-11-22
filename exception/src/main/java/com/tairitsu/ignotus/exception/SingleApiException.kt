package com.tairitsu.ignotus.exception

import kotlin.properties.Delegates

/**
 * 单条错误消息同时返回给前端时用的异常类
 */
open class SingleApiException : ApiException {
    var status by Delegates.notNull<Int>()
    private var code: String
    private var detail: String

    constructor(status: Int, code: String, detail: String) : super(detail) {
        this.status = status
        this.code = code
        this.detail = detail
    }

    open fun toJSONObject(): HashMap<String, Any> {
        val e = HashMap<String, Any>()
        e["status"] = status.toString()
        e["code"] = code
        e["detail"] = detail
        return e
    }

    override fun toJSONArray(): ArrayList<Any> {
        val e = ArrayList<Any>()
        e.add(toJSONObject())
        return e
    }
}
