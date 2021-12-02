package com.tairitsu.ignotus.support.util

import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

object ValidatorAttributesHelper {
    @Suppress("UNCHECKED_CAST")
    fun getAttributeFriendlyName(s: String) = Translation.service.getMessage(
        key = "validation-attributes.attributes.$s",
        args = Collections.EMPTY_MAP as Map<String, Any>,
        locale = LocaleContextHolder.getLocale(),
        default = s
    )
}
