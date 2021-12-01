package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.support.util.Translation.lang
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorString : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(lang("validation.string", mapOf("attribute" to attribute)))
            return
        }
    }
}

@Component
class ValidatorInt : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Int) {
            fail(lang("validation.integer", mapOf("attribute" to attribute)))
            return
        }
    }
}

@Component
open class ValidatorNumber : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Number) {
            fail(lang("validation.numeric", mapOf("attribute" to attribute)))
            return
        }
    }
}

@Component
open class ValidatorDouble : ValidatorNumber()

@Component
open class ValidatorBoolean : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Boolean) {
            fail(lang("validation.boolean", mapOf("attribute" to attribute)))
            return
        }
    }
}

@Component
open class ValidatorArray : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Collection<*>) {
            fail(lang("validation.array", mapOf("attribute" to attribute)))
            return
        }
    }
}

@Component
open class ValidatorObject : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Map<*, *>) {
            fail(lang("validation.object", mapOf("attribute" to attribute)))
            return
        }
    }
}
