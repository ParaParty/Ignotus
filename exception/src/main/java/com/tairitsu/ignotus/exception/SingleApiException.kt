package com.tairitsu.ignotus.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import java.util.*

/**
 * 单条错误消息同时返回给前端时用的异常类
 *
 * 一般业务异常从这里继承就行
 *
 * [见 JSON:API 文档](https://jsonapi.org/format/#error-objects)
 */
open class SingleApiException : ApiException {
    companion object {
        @JvmStatic
        val log: Logger = LoggerFactory.getLogger(SingleApiException::class.java)
    }

    /**
     * a unique identifier for this particular occurrence of the problem.
     */
    var id: String? = null
        protected set

    /**
     * a links object containing the following members:
     *
     * [links].about: a link that leads to further details about this particular occurrence of the problem.
     */
    var links: Map<String, String>? = null
        protected set

    /**
     * the HTTP status code applicable to this problem, expressed as a string value.
     */
    var status: HttpStatus protected set

    /**
     * an application-specific error code, expressed as a string value.
     */
    var code: String protected set

    /**
     * a short, human-readable summary of the problem that SHOULD NOT change from occurrence to occurrence of the problem, except for purposes of localization.
     */
    var title: String? = null
        protected set

    /**
     * a human-readable explanation specific to this occurrence of the problem. Like [title], this field’s value can be localized.
     */
    var detail: String protected set

    /**
     * source: an object containing references to the source of the error, optionally including any of the following members:
     *
     * [source].pointer: a JSON Pointer [RFC6901](https://tools.ietf.org/html/rfc6901) to the associated entity in the request document (e.g. "/data" for a primary data object, or "/data/attributes/title" for a specific attribute).
     * [source].parameter: a string indicating which URI query parameter caused the error.
     */
    var source: Map<String, String>? = null
        protected set

    /**
     * a [meta object](https://jsonapi.org/format/#document-meta) containing non-standard meta-information about the error.
     */
    var meta: Map<String, Any?>? = null
        protected set

    constructor(status: Int, code: String, detail: String) : super(detail) {
        this.status = HttpStatus.valueOf(status)
        this.code = code
        this.detail = detail
    }

    constructor(status: HttpStatus, code: String, detail: String) : super(detail) {
        this.status = status
        this.code = code
        this.detail = detail
    }

    constructor(status: Int, code: String, detail: String, cause: Throwable?) : super(detail, cause) {
        this.status = HttpStatus.valueOf(status)
        this.code = code
        this.detail = detail
    }

    constructor(status: HttpStatus, code: String, detail: String, cause: Throwable?) : super(detail, cause) {
        this.status = status
        this.code = code
        this.detail = detail
    }

    /**
     * 序列化为错误信息
     */
    fun toJSONObject(): Map<String, Any> {
        val e = LinkedHashMap<String, Any>()

        id.let {
            if (it != null) {
                e["id"] = it
            }
        }

        links.let {
            if (it != null) {
                e["links"] = it
            }
        }

        e["status"] = status.value().toString()

        e["code"] = code

        title.let {
            if (it != null) {
                e["title"] = it
            }
        }

        e["detail"] = detail

        source.let {
            if (it != null) {
                e["source"] = it
            }
        }

        meta.let {
            if (it != null) {
                e["meta"] = it
            }
        }

        return e
    }

    final override fun toJSONArray(): Iterable<Any> {
        return Collections.singletonList(toJSONObject())
    }

    final override fun getHttpStatus(): HttpStatus {
        if (this is LoggableException) {
            log.error(this.message ?: this.toString(), this)
        }
        return status
    }
}
