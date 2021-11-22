package com.tairitsu.ignotus.exception

/**
 * 多条错误消息同时返回给前端时用的异常类
 */
open class ApiExceptionBag : ApiException() {
    var exceptions: MutableList<SingleApiException> = mutableListOf();

    fun add(e: ApiException) {
        if (e is SingleApiException) {
            exceptions.add(e)
        }

        if (e is ApiExceptionBag) {
            e.exceptions.forEach { s -> exceptions.add(s) };
        }
    }

    override fun toJSONArray(): ArrayList<Any> {
        val ret = ArrayList<Any>()
        exceptions.forEach { s -> ret.add(s.toJSONObject()) }
        return ret;
    }

}
