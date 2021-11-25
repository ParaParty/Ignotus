package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页大小格式错误
 */
class PageSizeInvalidException : SingleApiException(404, "page_size_invalid", "")
