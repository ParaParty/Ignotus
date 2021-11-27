package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

/**
 * 业务异常：页码无效
 */
class PageInvalidException : SingleApiException(500,
    "page_invalid",
    "Unexpected exception. If occurred please contact the administrator of this site for more information.")
