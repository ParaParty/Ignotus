package com.tairitsu.ignotus.exception.relation

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 对象关系异常：不合法的排序方式
 * 用户提交的排序信息不在 Controller 定义的范围内
 */
class SortInvalidException : SingleApiException(400, "sort_invalid", "")
