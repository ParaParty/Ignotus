package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.support.util.Translation.lang
import com.tairitsu.ignotus.support.util.ValidatorAttributesHelper
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorUuid : AttributeValidatorInterface {
    private val check = "^[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}$"
    private val regex = Regex(check)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(lang("validation.string", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        if (!value.matches(regex)) fail(lang("validation.uuid", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))

    }
}

@Component
class ValidatorUuidWithoutDash : AttributeValidatorInterface {
    private val check = "^[0-9a-f]{32}$"
    private val regex = Regex(check)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(lang("validation.string", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        if (!value.matches(regex)) fail(lang("validation.uuid_without_dash", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
    }
}

