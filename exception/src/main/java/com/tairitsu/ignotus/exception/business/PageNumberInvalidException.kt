package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页码格式错误
 */
class PageNumberInvalidException : SingleApiException(400, "page_number_invalid", "")
