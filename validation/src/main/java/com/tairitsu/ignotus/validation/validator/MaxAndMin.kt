package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ValidatorMax : AttributeValidatorInterface {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (arg == null) {
            val s = "Validator ${this::javaClass.name} received a null argument."
            log.warn(s)
            fail(s)
            return
        }

        // TODO 回头再修这里的问题
        val maxValue: Double = when (arg) {
            is Number -> arg.toDouble()
            is Long -> arg.toDouble()
            is Double -> arg.toDouble()
            is String -> arg.toDouble()
            else -> {
                log.warn("Validator ${this::javaClass.name} can not parse argument of ${arg::javaClass.name} type.")
                0.0
            }
        }

        if (value == null) {
            fail("$attribute is null.")
            return
        }

        when (value) {
            is Number -> if (value.toDouble() > maxValue) fail("$value is bigger than $maxValue.")
            is String -> if (value.length > maxValue) fail("$attribute is longer than $maxValue.")
            is Iterable<*> -> if (value.count() > maxValue) fail("$attribute is shorter than $maxValue.")
            else -> log.warn("Validator ${this::javaClass.name} can not parse value of ${value::javaClass.name} type.")
        }

    }
}


@Component
class ValidatorMin : AttributeValidatorInterface {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (arg == null) {
            val s = "Validator ${this::javaClass.name} received a null argument."
            log.warn(s)
            fail(s)
            return
        }

        // TODO 回头再修这里的问题
        val minValue: Double = when (arg) {
            is Number -> arg.toDouble()
            is Long -> arg.toDouble()
            is Double -> arg.toDouble()
            is String -> arg.toDouble()
            else -> {
                log.warn("Validator ${this::javaClass.name} can not parse argument of ${arg::javaClass.name} type.")
                0.0
            }
        }

        if (value == null) {
            fail("$attribute is null.")
            return
        }

        when (value) {
            is Number -> if (value.toDouble() < minValue) fail("$value is smaller than $minValue.")
            is String -> if (value.length < minValue) fail("$attribute is shorter than $minValue.")
            is Iterable<*> -> if (value.count() < minValue) fail("$attribute is shorter than $minValue.")
            else -> log.warn("Validator ${this::javaClass.name} can not parse value of ${value::javaClass.name} type.")
        }

    }
}

