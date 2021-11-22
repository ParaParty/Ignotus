package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class ModelNotFoundException(detail: String = "") : SingleApiException(404, "model_not_found", detail)
