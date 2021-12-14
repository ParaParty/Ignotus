package com.tairitsu.ignotus.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * 多条错误消息同时返回给前端时用的异常类
 *
 * 如果想定义一个业务异常，一般从 [ApiException] 继承。
 */
open class ApiExceptionBag : ApiException() {
    companion object {
        @JvmStatic
        val log: Logger = LoggerFactory.getLogger(ApiExceptionBag::class.java)
    }

    val exceptions: ArrayList<SingleApiException> = ArrayList()

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

    final override fun toJSONArray(): Iterable<Any> {
        val ret = ArrayList<Any>()
        exceptions.forEach { s -> ret.add(s.toJSONObject()) }
        return ret
    }

    final override fun getHttpStatus(): HttpStatus {
        var ret = exceptions.firstOrNull()?.getHttpStatus() ?: HttpStatus.INTERNAL_SERVER_ERROR

        exceptions.forEach { s ->
            if (s.status != ret) {
                ret = HttpStatus.INTERNAL_SERVER_ERROR
            }

            if (s is LoggableException) {
                log.error(s.message ?: s.toString(), s)
            }
        }

        return ret
    }
}
