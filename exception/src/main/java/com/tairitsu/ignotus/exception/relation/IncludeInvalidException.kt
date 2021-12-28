package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 对象关系异常：不合法的包含关系
 * 用户提交的包含关系不在 Controller 定义的范围内
 */
class IncludeInvalidException : SingleApiException(400, CODE, Translation.translateDetail(CODE)) {
    companion object {
        const val CODE = "include_invalid"
    }

    init {
        this.title = Translation.translateTitle(CODE)

        val t = HashMap<String, String>()
        this.source = t
        t["parameter"] = "include"
    }
}
