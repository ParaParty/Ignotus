package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException
import com.tairitsu.ignotus.exception.business.ValidationInvalidException
import java.util.*

/**
 * 对象关系异常：不合法的关系
 */
class RelationshipInvalidException(
    reason: Reason,
    args: Map<String, Any> = Collections.emptyMap(),
) : SingleApiException(500, CODE, ""), LoggableException {

    companion object {
        private const val CODE = "relationship_invalid"
    }

    enum class Reason(val s: String) {
        INVALID_RELATED_OBJECT("invalid_related_object"),
        INVALID_RELATED_COLLECTION("invalid_related_collection"),
        EMPTY_RELATIONSHIP_NAME("empty_relationship_name"),
        RELATIONSHIP_METHOD_NOT_FOUND("relationship_method_not_found"),
    }

    init {
        this.title = Translation.translateTitle(CODE)

        this.detail = when (reason) {
            Reason.INVALID_RELATED_OBJECT ->
                Translation.translateDetail(ValidationInvalidException.CODE, reason.s, args)
            Reason.INVALID_RELATED_COLLECTION ->
                Translation.translateDetail(ValidationInvalidException.CODE, reason.s, args)
            Reason.EMPTY_RELATIONSHIP_NAME ->
                Translation.translateDetail(ValidationInvalidException.CODE, reason.s, args)
            Reason.RELATIONSHIP_METHOD_NOT_FOUND ->
                Translation.translateDetail(ValidationInvalidException.CODE, reason.s, args)
        }

        val t = HashMap<String, String>()
        this.source = t
        t["parameter"] = "include"
    }
}
