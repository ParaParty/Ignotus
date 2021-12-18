package com.tairitsu.ignotus.exception

/**
 * ApiException 报告器
 */
interface ApiExceptionReporter {

    /**
     * 报告 ApiException
     *
     * 本方法会在异常向客户端发送前调用。在这里可以对异常对象进行处理（如加入 trace id，记录日志等）。
     */
    fun report(exception: ApiException)
}

/**
 * 基本 ApiException 报告模板
 */
abstract class BasicApiExceptionReporter : ApiExceptionReporter {
    final override fun report(exception: ApiException) {
        when (exception) {
            is SingleApiException -> reportSingleApiException(exception)
            is ApiExceptionBag -> reportApiExceptionBag(exception)
            else -> reportUnknownApiException(exception)
        }
    }

    /**
     * 报告 SingleApiException
     */
    abstract fun reportSingleApiException(exception: SingleApiException)

    /**
     * 报告 ApiExceptionBag
     */
    abstract fun reportApiExceptionBag(exception: ApiExceptionBag)

    /**
     * 报告未知错误
     */
    abstract fun reportUnknownApiException(exception: ApiException)
}
