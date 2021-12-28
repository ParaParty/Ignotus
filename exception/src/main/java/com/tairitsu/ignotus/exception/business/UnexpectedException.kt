package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.ExceptionConfig
import com.tairitsu.ignotus.exception.SingleApiException
import org.apache.commons.lang3.exception.ExceptionUtils

/**
 * 业务异常：未知异常
 *
 * 未知异常不实现 [com.tairitsu.ignotus.exception.LoggableException] 接口的原因是 [UnexpectedException] 除了是
 * [com.tairitsu.ignotus.exception.ApiExceptionHandler] 中定义的全局兜底异常处理外，还允许被用户自己实例化并抛出。
 *
 * 在兜底的异常处理中我们需要记录日志，而用户自己实例化并抛出的可能并不需要记录日志。
 * （当然，我也建议有啥业务错误就自己继承一个 [com.tairitsu.ignotus.exception.SingleApiException] 再抛出会。这样更为合理）
 */
class UnexpectedException : SingleApiException {
    companion object {
        const val CODE = "internal_server_error"
    }

    private var name: String? = null
    private var exception: Throwable? = null

    constructor(detail: String) : super(500, CODE, detail)

    constructor(detail: String, exception: Throwable?) : super(500, CODE, detail, exception) {
        this.exception = exception
        initExceptionInformation()
    }

    constructor(exception: Throwable) : super(500, CODE, exception.message ?: "", exception) {
        this.exception = exception
        initExceptionInformation()
    }

    private fun initExceptionInformation() {
        this.name = exception!!.javaClass.name

        this.title = Translation.translateTitle(CODE)

        if (!ExceptionConfig.IS_DEBUG) {
            this.detail = Translation.translateDetail(CODE)
        }

        if (ExceptionConfig.IS_DEBUG) {
            meta = getMate()
        }
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
