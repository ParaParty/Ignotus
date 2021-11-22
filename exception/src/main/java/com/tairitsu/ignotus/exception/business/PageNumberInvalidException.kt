package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class PageNumberInvalidException : SingleApiException(404, "page_number_invalid", "")
