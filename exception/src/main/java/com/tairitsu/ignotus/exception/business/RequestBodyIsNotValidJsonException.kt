package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class RequestBodyIsNotValidJsonException : SingleApiException(400, "request_body_is_not_json", "")
