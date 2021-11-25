package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：请求体不是有效的 JSON 格式
 */
class RequestBodyIsNotValidJsonException : SingleApiException(400, "request_body_is_not_json", "")
