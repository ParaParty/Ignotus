package com.tairitsu.ignotus.exception

import com.tairitsu.ignotus.exception.business.UnexpectedException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        response.status = e.getHttpStatus().value()

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
        val reason = UnexpectedException(e)
        return apiExceptionHandler(req, response, reason)
    }
}
