package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.support.util.Translation
import com.tairitsu.ignotus.support.util.ValidatorArgumentsHelper
import com.tairitsu.ignotus.support.util.ValidatorAttributesHelper
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ValidatorMax : AttributeValidatorInterface {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (arg == null) {
            val s =
                Translation.lang("validator_error.unexpected_argument.null", mapOf("validator" to this::javaClass.name))
            log.warn(s)
            fail(s)
            return
        }

        if (value == null) {
            fail(Translation.lang("validation.required",
                mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        try {
            when (value) {
                is Double -> {
                    val maxValue = ValidatorArgumentsHelper.parseToDouble(arg)
                    if (value > maxValue) fail(Translation.lang("validation.max.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is Float -> {
                    val maxValue = ValidatorArgumentsHelper.parseToFloat(arg)
                    if (value > maxValue) fail(Translation.lang("validation.max.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is Int -> {
                    val maxValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value > maxValue) fail(Translation.lang("validation.max.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is Long -> {
                    val maxValue = ValidatorArgumentsHelper.parseToLong(arg)
                    if (value > maxValue) fail(Translation.lang("validation.max.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is Number -> {
                    val maxValue = ValidatorArgumentsHelper.parseToDouble(arg)
                    if (value.toDouble() > maxValue) fail(Translation.lang("validation.max.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is String -> {
                    val maxValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value.length > maxValue) fail(Translation.lang("validation.max.string",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                is Iterable<*> -> {
                    val maxValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value.count() > maxValue) fail(Translation.lang("validation.max.array",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "max" to maxValue)))
                }
                else -> log.warn("Validator ${this::javaClass.name} can not parse value of ${value::javaClass.name} type.")
            }
        } catch (e: NumberFormatException) {
            log.warn("Validator ${this::javaClass.name} can not parse argument ${arg::javaClass.name} $arg")
            fail(Translation.lang("validator_error.unexpected_argument.general", mapOf("validator" to this::javaClass.name)))
        }
    }
}


@Component
class ValidatorMin : AttributeValidatorInterface {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (arg == null) {
            val s =
                Translation.lang("validator_error.unexpected_argument.null", mapOf("validator" to this::javaClass.name))
            log.warn(s)
            fail(s)
            return
        }

        if (value == null) {
            fail(Translation.lang("validation.required",
                mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute))))
            return
        }

        try {
            when (value) {
                is Double -> {
                    val minValue = ValidatorArgumentsHelper.parseToDouble(arg)
                    if (value < minValue) fail(Translation.lang("validation.min.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is Float -> {
                    val minValue = ValidatorArgumentsHelper.parseToFloat(arg)
                    if (value < minValue) fail(Translation.lang("validation.min.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is Int -> {
                    val minValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value < minValue) fail(Translation.lang("validation.min.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is Long -> {
                    val minValue = ValidatorArgumentsHelper.parseToLong(arg)
                    if (value < minValue) fail(Translation.lang("validation.min.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is Number -> {
                    val minValue = ValidatorArgumentsHelper.parseToDouble(arg)
                    if (value.toDouble() < minValue) fail(Translation.lang("validation.min.numeric",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is String -> {
                    val minValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value.length < minValue) fail(Translation.lang("validation.min.string",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                is Iterable<*> -> {
                    val minValue = ValidatorArgumentsHelper.parseToInt(arg)
                    if (value.count() < minValue) fail(Translation.lang("validation.min.array",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(attribute),
                            "min" to minValue)))
                }
                else -> log.warn("Validator ${this::javaClass.name} can not parse value of ${value::javaClass.name} type.")
            }
        } catch (e: NumberFormatException) {
            log.warn("Validator ${this::javaClass.name} can not parse argument ${arg::javaClass.name} $arg")
            fail(Translation.lang("validator_error.unexpected_argument.general", mapOf("validator" to this::javaClass.name)))
        }
    }
}

