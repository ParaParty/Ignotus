package com.tairitsu.ignotus.serializer

import com.tairitsu.ignotus.serializer.vo.BaseResponse
import com.tairitsu.ignotus.serializer.vo.RootResponse
import com.tairitsu.ignotus.support.util.ServletRequestExtension.getExtractedInclude
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@Component
@RestControllerAdvice
class ResponseHandler : ResponseBodyAdvice<Any?> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>?>?): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        data: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>?>?,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        var skip = false

        val servletRequest = (request as ServletServerHttpRequest).servletRequest

        @Suppress("UNCHECKED_CAST")
        val extractedInclude = servletRequest.getExtractedInclude() ?: emptySet()

        when (data) {
            is BaseResponse -> BaseResponse.loadRelationship(
                data,
                extractedInclude
            )
            is Collection<*> -> {
                BaseResponse.loadRelationship(data, extractedInclude)
                if (data.isNotEmpty() && data.first() !is BaseResponse) {
                    skip = true
                }
            }
            is RootResponse -> {
                data.data?.let { dataCopy ->
                    when (dataCopy) {
                        is BaseResponse -> BaseResponse.loadRelationship(
                            dataCopy,
                            extractedInclude
                        )
                        is Collection<*> -> {
                            BaseResponse.loadRelationship(dataCopy, extractedInclude)
                            if (dataCopy.isNotEmpty() && dataCopy.first() !is BaseResponse) {
                                skip = true
                            }
                        }
                        else -> {
                            skip = true
                            // throw RelationshipInvalidException("Api result is not a BaseResponse or a set of BaseResponses.")
                        }
                    }
                }
            }
            else -> {
                skip = true
                // throw RelationshipInvalidException("Api result is not a BaseResponse or a set of BaseResponses.")
            }
        }

        if (servletRequest.getAttribute("api_exception_handler") == null) {
            val httpStatus = when (servletRequest.method) {
                "GET" -> HttpStatus.OK
                "POST" -> HttpStatus.CREATED
                "DELETE" -> if (data == null) HttpStatus.NO_CONTENT else HttpStatus.OK
                "PATCH" -> HttpStatus.OK
                "PUT" -> HttpStatus.OK
                else -> HttpStatus.OK
            }
            response.setStatusCode(httpStatus)
        }

        if (skip) return data

        val ret = Serializer.serializeTopLevel(data!!, servletRequest)

        return ret
    }
}
