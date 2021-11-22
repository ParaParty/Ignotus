package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.exception.LoggableException
import com.tairitsu.ignotus.exception.SingleApiException

class ControllerInvalidException(detail: String) : SingleApiException(500, "controller_invalid_exception", detail),
    LoggableException {
    companion object {
        fun basicParametersMissing(): ControllerInvalidException =
            ControllerInvalidException("Please ensure `request: HttpServletRequest` were declared in the parameters list.")

        fun bodyParametersMissing(): ControllerInvalidException =
            ControllerInvalidException("Please ensure `@RequestBody body: String?` was declared in the parameters list.")
    }
}
