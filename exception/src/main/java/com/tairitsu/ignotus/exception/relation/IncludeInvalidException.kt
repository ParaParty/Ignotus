package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 对象关系异常：不合法的包含关系
 * 用户提交的包含关系不在 Controller 定义的范围内
 */
class IncludeInvalidException : SingleApiException(400, "include_invalid", "")
