package com.tairitsu.ignotus.validation.validator

import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ValidatorIn : AttributeValidatorInterface {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun invoke(attribute: String, arg: Any?, value: Any?, fail: (String) -> Unit) {
        if (arg == null) {
            val s = "Validator ${this::javaClass.name} received a null argument."
            log.warn(s)
            fail(s)
            return
        }

        if (arg is Collection<*>) {
            if (!arg.contains(value)) {
                fail("$attribute must be one of ${arg.joinToString(",", "[", "]")}")
            }
            return
        }

        if (arg is String && value is String) {
            arg.split(",").also { collect ->
                if (!collect.contains(value)) {
                    fail("$attribute must be one of ${collect.joinToString(",", "[", "]")}")
                }
            }
            return
        }

        if (arg is String && (value is Byte || value is Short || value is Int || value is Long)) {
            arg.split(",").map { s -> s.toLong() }.also { collect ->
                if (!collect.contains(value)) {
                    fail("$attribute must be one of ${collect.joinToString(",", "[", "]")}")
                }
            }
            return
        }

        if (arg is String && (value is Float || value is Double)) {
            arg.split(",").map { s -> s.toDouble() }.also { collect ->
                if (!collect.contains(value)) {
                    fail("$attribute must be one of ${collect.joinToString(",", "[", "]")}")
                }
            }
            return
        }

        val s = "Validator ${this::javaClass.name} received a nun collection argument."
        log.warn(s)
        fail(s)
        return
    }
}
