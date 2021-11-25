package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 对象关系异常：不合法的关系
 */
class RelationshipInvalidException : SingleApiException, LoggableException {
    constructor(detail: String) : super(500, "relationship_invalid", detail)
}
