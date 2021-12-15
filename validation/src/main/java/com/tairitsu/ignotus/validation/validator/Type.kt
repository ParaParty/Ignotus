package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.support.util.Translation
import com.tairitsu.ignotus.support.util.ValidatorAttributesHelper
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.springframework.stereotype.Component

@Component
class ValidatorString : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is String) {
            fail(Translation.lang("validation.string", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }
    }
}

@Component
class ValidatorInt : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Int) {
            fail(Translation.lang("validation.integer", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }
    }
}

@Component
open class ValidatorNumber : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Number) {
            fail(Translation.lang("validation.numeric", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
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
            fail(Translation.lang("validation.boolean", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }
    }
}

@Component
open class ValidatorArray : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Collection<*>) {
            fail(Translation.lang("validation.array", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }
    }
}

@Component
open class ValidatorObject : AttributeValidatorInterface {
    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (value !is Map<*, *>) {
            fail(Translation.lang("validation.object", mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }
    }
}
