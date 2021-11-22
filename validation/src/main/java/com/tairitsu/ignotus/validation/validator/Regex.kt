package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorRegex : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail("$attribute is not a string");
            return
        }

        val check = arg.toRegex(fail) ?: return
        if (!value.matches((check))) fail("$attribute is an instance of ${check.pattern}");
    }
}

@Component
class ValidatorNotRegex : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail("$attribute is not a string");
            return
        }

        val check = arg.toRegex(fail) ?: return
        if (value.matches((check))) fail("$attribute is an instance of ${check.pattern}");
    }
}

private fun Any?.toRegex(fail: (String) -> Unit): Regex? =
    when (this) {
        is String -> {
            Regex(this)
        }
        is Regex -> {
            this
        }
        else -> {
            fail("arg is not a regex");
            null
        }
    }
