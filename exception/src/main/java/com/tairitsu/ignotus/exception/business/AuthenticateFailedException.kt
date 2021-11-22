package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.SingleApiException

class AuthenticateFailedException() : SingleApiException(403, "authenticate_failed", "") {
}
