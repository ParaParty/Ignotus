package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorString : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail("$attribute is not a string")
            return
        }
    }
}

@Component
class ValidatorInt : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Int) {
            fail("$attribute is not a number")
            return
        }
    }
}

@Component
open class ValidatorNumber : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Number) {
            fail("$attribute is not a number")
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
            fail("$attribute is not a Boolean")
            return
        }
    }
}

@Component
open class ValidatorArray : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Collection<*>) {
            fail("$attribute is not an array")
            return
        }
    }
}

@Component
open class ValidatorObject : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Map<*, *>) {
            fail("$attribute is not an object")
            return
        }
    }
}
