package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class PageOffsetInvalidException : SingleApiException(404, "page_offset_invalid", "")
