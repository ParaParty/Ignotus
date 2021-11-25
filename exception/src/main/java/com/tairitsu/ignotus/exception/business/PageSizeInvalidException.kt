package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class PageSizeInvalidException : SingleApiException(404, "page_size_invalid", "")
