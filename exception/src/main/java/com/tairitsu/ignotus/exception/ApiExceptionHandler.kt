package com.tairitsu.ignotus.exception

import com.tairitsu.ignotus.exception.business.UnexpectedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 全局异常处理
 */
@ControllerAdvice
open class ApiExceptionHandler {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(value = [ApiException::class])
    @ResponseBody
    open fun apiExceptionHandler(
        req: HttpServletRequest,
        response: HttpServletResponse,
        e: ApiException,
    ): Map<String, Any> {
        val obj = HashMap<String, Any>()
        obj["errors"] = e.toJSONArray()

        var status: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()

        if (e is SingleApiException) {
            status = e.status
            if (e is LoggableException) {
                log.error(e.message ?: e.toString(), e)
            }
        }

        if (e is ApiExceptionBag) {
            status = e.exceptions[0].status
            e.exceptions.forEach { s ->
                if (s.status != status) {
                    status = 500
                }

                if (s is LoggableException) {
                    log.error(s.message ?: s.toString(), s)
                }
            }
        }

        response.status = status
        req.setAttribute("api_exception_handler", true)
        return obj
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    open fun unexpectedExceptionHandler(
        req: HttpServletRequest,
        response: HttpServletResponse,
        e: Exception,
    ): Map<String, Any> {
        log.error(e.message ?: e.toString(), e)
        val reason = UnexpectedException(e.message ?: "", e)
        return apiExceptionHandler(req, response, reason)
    }
}
