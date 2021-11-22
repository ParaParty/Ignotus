package com.tairitsu.ignotus.exception.mq

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

class MQProcessingException(detail: String) : SingleApiException(404, "mq_processing_exception", detail),
    LoggableException
