package com.tairitsu.ignotus.foundation

import com.tairitsu.ignotus.exception.business.PageInvalidException
import com.tairitsu.ignotus.exception.business.PageNumberInvalidException
import com.tairitsu.ignotus.exception.business.PageOffsetInvalidException
import com.tairitsu.ignotus.exception.business.PageSizeInvalidException
import com.tairitsu.ignotus.exception.relation.IncludeInvalidException
import com.tairitsu.ignotus.exception.relation.SortInvalidException
import com.tairitsu.ignotus.foundation.annotation.JsonApiController
import com.tairitsu.ignotus.support.model.vo.OffsetBasedPagination
import com.tairitsu.ignotus.support.util.ServletRequestExtension.getExtractedInclude
import com.tairitsu.ignotus.support.util.ServletRequestExtension.setExtractedFilter
import com.tairitsu.ignotus.support.util.ServletRequestExtension.setExtractedInclude
import com.tairitsu.ignotus.support.util.ServletRequestExtension.setExtractedPagination
import com.tairitsu.ignotus.support.util.ServletRequestExtension.setExtractedSort
import com.tairitsu.ignotus.validation.Valid
import com.tairitsu.ignotus.validation.Validator
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import javax.servlet.http.HttpServletRequest

@Aspect
@Component
open class ControllerAspect {

    @Autowired
    private lateinit var foundationConfig: FoundationConfig

    @Autowired
    private lateinit var validator: Validator

    @Before("@annotation(jsonApiController)")
    fun beforeHandleRequest(joinPoint: JoinPoint, jsonApiController: JsonApiController) {
        // 获取方法信息
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val parametersList = method.parameters
        val parametersCount = joinPoint.args.size

        // 定义变量
        var allRequestParams: Map<String, String>? = null
        val requestAttributes = RequestContextHolder.getRequestAttributes()
        val request = requestAttributes!!.resolveReference(RequestAttributes.REFERENCE_REQUEST) as HttpServletRequest

        if (allRequestParams == null) {
            allRequestParams = HashMap()
            request.parameterMap.forEach { (k, v) ->
                allRequestParams.put(k, v.joinToString(","))
            }
        }

        // 根据 Json:Api 规则， POST 和 PATCH 的请求请求体必须为 json 格式。
//        if (request.method in listOf("POST", "PATCH")) {
//            try {
//                val r = request.readBodyToString()
//                val mapper = ObjectMapper()
//                mapper.readTree(r)
//            } catch (e: JsonProcessingException) {
//                throw RequestBodyIsNotValidJsonException()
//            } catch (e: JsonMappingException) {
//                throw RequestBodyIsNotValidJsonException()
//            } catch (e: NullPointerException) {
//                throw RequestBodyIsNotValidJsonException()
//            }
//        }

        // 解析 Controller 参数
        for (i in 0 until parametersCount) {
            val parameter = parametersList[i]
            val argument = joinPoint.args[i]

            var isRequestBody = false
            var isValid = false

            // 扫描注解
            val annotations = parameter.declaredAnnotations
            for (annotation in annotations) {
                if (annotation is RequestBody) {
                    isRequestBody = true
                }
                if (annotation is Valid) {
                    isValid = true
                }
            }

            if (isRequestBody && isValid) {
                validator.validate(argument)
            }
        }

        // 开始处理
        extractInclude(jsonApiController.optionalInclude.toList(), request, allRequestParams)
        extractFilter(request, allRequestParams)
        val sort = extractSort(jsonApiController.optionalSort.toList(), request, allRequestParams)
        extractPage(request, allRequestParams, sort)
    }

    /**
     * 从请求中解析关联关系
     */
    private fun extractInclude(
        optionalInclude: List<String>,
        request: HttpServletRequest,
        allRequestParams: Map<String, String>,
    ): Set<String> {
        val tx = request.getExtractedInclude()
        if (tx is Set<*>) {
            return tx
        }

        var ret = ArrayList<String>()
        val includeStr = allRequestParams["include"]
        if (!includeStr.isNullOrEmpty()) {
            ret = ArrayList((allRequestParams["include"] ?: "").split(','))
        }

        val available = ArrayList(optionalInclude)
        ret.forEach(fun(s: String) {
            if (!available.contains(s)) {
                throw IncludeInvalidException()
            }
        })

        val result = ret.toSet()
        request.setExtractedInclude(result)
        return result
    }

    /**
     * 从请求中解析查询筛选器设置
     */
    private fun extractFilter(
        request: HttpServletRequest,
        allRequestParams: Map<String, String>,
    ): Map<String, String> {
        val ret = HashMap<String, String>()

        allRequestParams.forEach { (k, v) ->
            if (k.startsWith("filter[") && k.endsWith("]")) {
                val s = k.substring(7, k.length - 1)
                if (!(s.contains('[') || s.contains(']'))) {
                    ret[s] = v
                }
            }
        }

        request.setExtractedFilter(ret)
        return ret
    }

    /**
     * 从请求中解析查询排序设置
     */
    private fun extractSort(
        optionalSort: List<String>,
        request: HttpServletRequest,
        allRequestParams: Map<String, String>,
    ): Sort {
        val ret = ArrayList<Sort.Order>()

        val sortStr = allRequestParams["sort"] ?: ""
        val sortArr = sortStr.split(",")
        sortArr.forEach { s ->
            if (s.isNotBlank()) {
                val col: String
                val order = when {
                    s.startsWith('-') -> {
                        col = s.substring(1)
                        "DESC"
                    }
                    s.startsWith('+') -> {
                        col = s.substring(1)
                        "ASC"
                    }
                    else -> {
                        col = s
                        "ASC"
                    }
                }

                if (col !in optionalSort) {
                    throw SortInvalidException()
                }

                if (order == "ASC") {
                    ret.add(Sort.Order.asc(col))
                } else {
                    ret.add(Sort.Order.desc(col))
                }
            }
        }

        val sort = Sort.by(ret)
        request.setExtractedSort(sort)
        return sort
    }

    /**
     * 从请求中解析分页设置
     */
    private fun extractPage(
        request: HttpServletRequest,
        allRequestParams: Map<String, String>,
        sort: Sort,
    ): Pageable {

        val limitParameterName = if (allRequestParams.containsKey("page[limit]")) {
            "page[limit]"
        } else if (allRequestParams.containsKey("page[size]")) {
            "page[size]"
        } else {
            null
        }
        val limitStr = if (limitParameterName == null) {
            null
        } else {
            allRequestParams[limitParameterName]
        }
        val limit = if (limitStr == null) {
            foundationConfig.pagination.defaultLimit
        } else {
            limitStr.toIntOrNull() ?: throw PageSizeInvalidException(limitParameterName ?: "", PageSizeInvalidException.Reason.FAIL_TO_PARSE)
        }
        if (limit < foundationConfig.pagination.minLimit || limit > foundationConfig.pagination.maxLimit) {
            throw PageSizeInvalidException(limitParameterName ?: "",
                PageSizeInvalidException.Reason.OUT_OF_RANGE,
                minLimit = foundationConfig.pagination.minLimit,
                maxLimit = foundationConfig.pagination.maxLimit,
            )
        }

        // 基于页码的分页
        if (foundationConfig.pagination.pageBased.enabled) {
            val pageNumberStr = allRequestParams["page[number]"]
            if (pageNumberStr != null) {
                val number = pageNumberStr.toIntOrNull() ?: throw PageNumberInvalidException("page[number]",
                    PageNumberInvalidException.Reason.FAIL_TO_PARSE)
                val ret = when (foundationConfig.pagination.pageBased.startAt) {
                    0 -> if (number >= 0) {
                        PageRequest.of(number, limit, sort)
                    } else {
                        throw PageNumberInvalidException("page[number]",
                            PageNumberInvalidException.Reason.OUT_OF_RANGE,
                            0)
                    }
                    1 -> if (number >= 1) {
                        PageRequest.of(number - 1, limit, sort)
                    } else {
                        throw PageNumberInvalidException(
                            "page[number]",
                            PageNumberInvalidException.Reason.OUT_OF_RANGE,
                            1)
                    }
                    else -> throw PageInvalidException(PageInvalidException.Reason.START_AT_ERROR)
                }
                request.setExtractedPagination(ret)
                return ret
            }
        }

        // 基于起始位置的分页
        if (foundationConfig.pagination.offsetBased.enabled) {
            val offsetNumberStr = allRequestParams["page[offset]"] ?: "0"
            val offset = offsetNumberStr.toIntOrNull() ?: throw PageOffsetInvalidException("page[offset]",
                PageOffsetInvalidException.Reason.FAIL_TO_PARSE)
            if (offset < 0) {
                throw PageOffsetInvalidException("page[offset]", PageOffsetInvalidException.Reason.OUT_OF_RANGE)
            }
            val ret = OffsetBasedPagination(offset, limit, sort)
            request.setExtractedPagination(ret)
            return ret
        }

        val ret = OffsetBasedPagination(0, foundationConfig.pagination.defaultLimit)
        request.setExtractedPagination(ret)
        return ret
    }
}
