package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.ExceptionConfig
import com.tairitsu.ignotus.exception.SingleApiException
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * 业务异常：未知异常
 */
class UnexpectedException : SingleApiException {
    private var name: String?
    private var exception: Throwable?

    constructor(detail: String, exception: Throwable? = null) : super(500, "internal_server_error", detail) {
        this.exception = exception
        this.name = exception?.javaClass?.name

        if (!ExceptionConfig.IS_DEBUG) {
            this.detail = "internal_server_error"
        }
    }

    override fun toJSONObject(): HashMap<String, Any> {
        val e = super.toJSONObject()
        if (exception != null && ExceptionConfig.IS_DEBUG) {
            e["meta"] = getMate()
        }
        return e
    }

    private fun getMate(): HashMap<String, Any?> {
        val ret = HashMap<String, Any?>()
        ret["stackTrace"] = getOutputStackTrace()
        ret["name"] = name
        return ret
    }

    private fun getOutputStackTrace(): ArrayList<Any> {
        val ret = ArrayList<Any>()

        if (exception == null) {
            return ret
        }

        val e: Throwable? = ExceptionUtils.getRootCause(exception)
        val exceptionForPrinting = e ?: exception!!

        val stack = exceptionForPrinting.stackTrace
        for (callInfo in stack) {
            ret.add(callInfo.toString())
        }
        return ret
    }

}
