package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页偏移格式错误
 */
class PageOffsetInvalidException : SingleApiException(404, "page_offset_invalid", "")
