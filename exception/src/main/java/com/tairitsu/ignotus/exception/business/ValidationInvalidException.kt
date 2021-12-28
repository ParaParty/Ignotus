package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException
import org.springframework.http.HttpStatus
import java.util.*

/**
 * 业务异常：验证器错误
 */
class ValidationInvalidException(reason: Reason, args: Map<String, Any> = Collections.emptyMap()) :
    SingleApiException(404, CODE, ""),
    LoggableException {

    companion object {
        const val CODE = "validation_invalid_exception"
    }

    enum class Reason(val s: String) {
        NULL_CONTENT("null_content"),
        INVALID_VALIDATION_RULE("invalid_validation_rule"),
        VALIDATOR_DUPLICATED("validator_duplicated"),
        VALIDATOR_NOT_FOUND("validator_not_found"),
    }

    init {
        this.title = Translation.translateTitle(CODE)
        this.detail = when (reason) {
            Reason.NULL_CONTENT -> Translation.translateDetail(CODE, reason.s)
            Reason.INVALID_VALIDATION_RULE -> Translation.translateDetail(CODE, reason.s, args)
            Reason.VALIDATOR_DUPLICATED -> {
                this.status = HttpStatus.INTERNAL_SERVER_ERROR
                Translation.translateDetail(CODE, reason.s, args)
            }
            Reason.VALIDATOR_NOT_FOUND -> Translation.translateDetail(CODE, reason.s, args)
        }
    }
}
