package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

class RelationshipInvalidException : SingleApiException, LoggableException {
    constructor(detail: String) : super(500, "relationship_invalid", detail)
}
