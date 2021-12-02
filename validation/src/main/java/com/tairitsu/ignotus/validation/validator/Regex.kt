package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.support.util.Translation
import com.tairitsu.ignotus.support.util.Translation.lang
import com.tairitsu.ignotus.support.util.ValidatorAttributesHelper
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorRegex : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(lang("validation.string", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        val check = arg.toRegex(this.javaClass.name, fail) ?: return
        if (!value.matches((check))) fail(lang("validation.regex", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))));
    }
}

@Component
class ValidatorNotRegex : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(lang("validation.string", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        val check = arg.toRegex(this.javaClass.name, fail) ?: return
        if (value.matches((check))) fail(lang("validation.not_regex", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))));
    }
}

private fun Any?.toRegex(validatorName: String, fail: (String) -> Unit): Regex? =
    when (this) {
        is String -> {
            Regex(this)
        }
        is Regex -> {
            this
        }
        else -> {
            fail(lang("validation.validator_error.unexpected_argument.not_regex",
                mapOf("validator" to validatorName)))
            null
        }
    }
