package com.tairitsu.ignotus.exception

/**
 * 多条错误消息同时返回给前端时用的异常类
 */
open class ApiExceptionBag : ApiException() {
    var exceptions: MutableList<SingleApiException> = mutableListOf();

    /**
     * 添加单条错误消息
     */
    fun add(e: SingleApiException) {
        exceptions.add(e)
    }

    /**
     * 添加多条错误消息
     */
    fun add(e: ApiExceptionBag) {
        e.exceptions.forEach { s -> exceptions.add(s) };
    }

    override fun toJSONArray(): Iterable<Any> {
        val ret = ArrayList<Any>()
        exceptions.forEach { s -> ret.add(s.toJSONObject()) }
        return ret
    }
}
