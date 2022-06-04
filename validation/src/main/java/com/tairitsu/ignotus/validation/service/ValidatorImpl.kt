package com.tairitsu.ignotus.validation.service

import com.fasterxml.jackson.core.type.TypeReference
import com.tairitsu.ignotus.exception.ApiExceptionBag
import com.tairitsu.ignotus.exception.SingleApiException
import com.tairitsu.ignotus.exception.business.ValidateException
import com.tairitsu.ignotus.exception.business.ValidationInvalidException
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import com.tairitsu.ignotus.support.service.JSONMapperRegister
import com.tairitsu.ignotus.support.util.Translation
import com.tairitsu.ignotus.support.util.ValidatorAttributesHelper
import com.tairitsu.ignotus.support.util.toGetterFunction
import com.tairitsu.ignotus.validation.AttributeValidatorInterface
import com.tairitsu.ignotus.validation.Validator
import com.tairitsu.ignotus.validation.ValidatorAnnotation
import com.tairitsu.ignotus.validation.annotation.Required
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter


@Component
@Lazy
class ValidatorImpl : Validator {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    /**
     * 验证器合集
     */
    private val validatorPool by lazy {
        val ret = HashMap<String, AttributeValidatorInterface>()

        val beans: Map<String, AttributeValidatorInterface> =
            applicationContext.getBeansOfType(AttributeValidatorInterface::class.java)

        beans.forEach { (k, v) ->
            val name = run {
                val s = if (k.startsWith("validator") || k.startsWith("Validator")) {
                    k.substring("validator".length)
                } else {
                    k
                }

                if (s.length == 1) {
                    s.lowercase()
                } else {
                    s[0].lowercase() + s.substring(1)
                }
            }

            if (ret[name] != null) {
                throw ValidationInvalidException(ValidationInvalidException.Reason.VALIDATOR_DUPLICATED,
                    mapOf("validator" to name))
            }
            ret[name] = v
        }

        ret
    }

    override fun validate(content: Any?, basePath: String) {
        if (content == null) {
            return
        }

        val exception = ArrayList<SingleApiException>()

        validateAnObject(content, basePath, exception)

        if (exception.isNotEmpty()) {
            val e = ApiExceptionBag()
            exception.forEach { s -> e.add(s); }
            throw e
        }
    }

    private fun validateAnObject(content: Any, basePath: String = "", exception: ArrayList<SingleApiException>) {
        val kType = content::class
        val fields = kType.declaredMemberProperties

        for (field in fields) {
            val javaField = field.javaField ?: continue
            field.isAccessible = true
            javaField.isAccessible = true

            var done = false
            try {
                val value = field.getter.call(content)
                validateSingleField(javaField, value, basePath, exception)
                done = true
            } catch (_: InvocationTargetException) {

            } catch (_: IllegalAccessException) {

            } catch (e: Exception) {
                log.error(e.message, e)
            }

            if (done) {
                continue
            }

            try {
                val getMethod = field.javaGetter ?: kType.java.getMethod(field.name.toGetterFunction())
                val value = getMethod.invoke(content)
                validateSingleField(javaField, value, basePath, exception)
                done = true
            } catch (_: InvocationTargetException) {

            } catch (_: UninitializedPropertyAccessException) {

            } catch (_: NoSuchMethodException) {

            } catch (_: SecurityException) {

            } catch (_: SecurityException) {

            } catch (e: Exception) {
                log.error(e.message, e)
            }

            if (done) {
                continue
            }

            validateSingleField(javaField, null, basePath, exception)
        }
    }

    private fun validateSingleField(field: Field, value: Any?, key: String, exception: ArrayList<SingleApiException>) {
        val step = field.name
        val newKey = "$key/$step"

        val annotations = field.annotations
        for (annotation in annotations) {
            val proxiedAnnotationType = annotation.javaClass
            val proxiedAnnotationMethods = proxiedAnnotationType.declaredMethods.associateBy { it.name }
            val annotationType =
                (proxiedAnnotationMethods["annotationType"]?.invoke(annotation) ?: continue) as Class<*>

            if (annotationType.getDeclaredAnnotation(ValidatorAnnotation::class.java) == null) {
                continue
            }

            if (annotation is Required) {
                if (value == null) {
                    exception.add(ValidateException(Translation.lang("validation.required",
                        mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(step))), newKey))
                }
            } else if (value != null) {
                val name = run {
                    val s = annotationType.simpleName

                    if (s.length == 1) {
                        s.lowercase()
                    } else {
                        s[0].lowercase() + s.substring(1)
                    }
                }
                val validator =
                    validatorPool[name]
                        ?: throw ValidationInvalidException(ValidationInvalidException.Reason.VALIDATOR_NOT_FOUND,
                            mapOf("validator" to name))

                val arg = proxiedAnnotationMethods["value"]?.invoke(annotation)
                validator.invoke(step, arg, value) { s ->
                    exception.add(ValidateException(s, newKey))
                }
            }
        }

        val fieldType = field.type
        when {
            Number::class.java.isAssignableFrom(fieldType) -> Unit
            String::class.java.isAssignableFrom(fieldType) -> Unit
            Collection::class.java.isAssignableFrom(fieldType) -> Unit
            LocalTime::class.java.isAssignableFrom(fieldType) -> Unit
            Date::class.java.isAssignableFrom(fieldType) -> Unit
            else -> {
                if (value == null) {
                    return
                }

                validateAnObject(value, newKey, exception)
            }
        }
    }

    override fun <T : Any> validate(
        content: String?,
        validation: Map<String, Any>,
        basePath: String,
        type: KClass<T>,
    ): T {
        return validate(content, validation, basePath, type.java)
    }

    override fun <T> validate(content: String?, validation: Map<String, Any>, basePath: String, type: Class<T>): T {
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        val tree = validateStringAndReturnMap(content, validation, basePath)
        return mapper.convertValue(tree, type)
    }

    override fun <T> validate(content: String?, validation: Map<String, Any>, basePath: String, type: TypeReference<T>): T {
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        val tree = validateStringAndReturnMap(content, validation, basePath)
        return mapper.convertValue(tree, type)
    }

    override fun validate(content: String?, validation: Map<String, Any>, basePath: String) {
        validateStringAndReturnMap(content, validation, basePath)
    }

    private fun validateStringAndReturnMap(content: String?, validation: Map<String, Any>, basePath: String): Map<String, Any?> {
        if (content == null) {
            throw ValidationInvalidException(ValidationInvalidException.Reason.NULL_CONTENT)
        }
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        val typeRef = object : TypeReference<HashMap<String, Any?>>() {}
        val tree = mapper.readValue(content, typeRef)
        this.validate(tree, validation, basePath)
        return tree
    }

    override fun <T> validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String, type: Class<T>): T {
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        validate(content, validation, basePath)
        return mapper.convertValue(content, type)
    }

    override fun <T: Any> validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String, type: KClass<T>): T {
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        validate(content, validation, basePath)
        return mapper.convertValue(content, type.java)
    }

    override fun <T> validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String, type: TypeReference<T>): T {
        val mapper = JSONMapperRegister.objectMapperProvider.objectMapper
        validate(content, validation, basePath)
        return mapper.convertValue(content, type)
    }

    override fun validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String) {
        validation.forEach { (k, v) ->
            if (
                ((v !is String) && (v !is Collection<*>) && (v !is AttributeValidatorInterface) && (v !is Pair<*, *>))
            ) {
                throw ValidationInvalidException(ValidationInvalidException.Reason.INVALID_VALIDATION_RULE,
                    mapOf("attribute" to k))
            }
        }

        val result = ArrayList<SingleApiException>()

        validation.forEach { (k, v) ->
            result.addAll(validateSingleRecord(content, k, v, basePath))
        }

        if (result.isNotEmpty()) {
            val e = ApiExceptionBag()
            result.forEach { s -> e.add(s); }
            throw e
        }
    }

    private fun validateSingleRecord(
        content: Map<String, Any?>,
        key: String,
        rule: Any,
        basePath: String,
    ): Collection<SingleApiException> {
        val rules = ArrayList<Any>()
        when (rule) {
            is String -> rules.addAll(rule.split('|'))
            is Collection<*> -> rule.forEach { s -> rules.add(s!!) }
            is Pair<*, *> -> rules.add(rule)
            is AttributeValidatorInterface -> rules.add(rule)
            else -> throw ValidationInvalidException(ValidationInvalidException.Reason.INVALID_VALIDATION_RULE,
                mapOf("attribute" to key))
        }
        val path = key.split('.')

        return validateSingleParsedRecord(content, basePath, path, rules)
    }

    private fun validateSingleParsedRecord(
        content: Map<String, Any?>,
        key: String,
        path: List<String>,
        rules: List<Any>,
    ): Collection<SingleApiException> {
        val ret = ArrayList<SingleApiException>()
        val step = JacksonNamingStrategyConfig.namingStrategy?.nameForField(null, null, path[0]) ?: path[0]
        val newKey = "$key/$step"

        if (!content.containsKey(step)) {
            rules.forEach { rule ->
                if (rule is String) {
                    val t = rule.split(Regex(":"), 1)
                    val validatorName = t[0]
                    if (validatorName == "required") {
                        ret.add(ValidateException(Translation.lang("validation.required",
                            mapOf("attribute" to ValidatorAttributesHelper.getAttributeFriendlyName(step))), newKey))
                    }
                }
            }
        } else if (path.size == 1) {
            val value = content[step]
            rules.forEach { rule ->
                if (rule is String) {
                    // 字符串形式的校验器，字符串表示校验器名

                    val t = rule.split(Regex(":"), 2)
                    val validatorName = t[0]
                    val validatorParam = if (t.size > 1) t[1] else ""

                    // step 被检查的属性
                    // newKey 被检查的路径（pointer 部分）

                    // value 被检查的值
                    // validatorName 被检查的规则校验器名
                    // validatorParam 被检查的规则校验器参数

                    val validator = validatorPool[validatorName]
                    if (validator == null) {
                        ret.add(ValidationInvalidException(ValidationInvalidException.Reason.VALIDATOR_NOT_FOUND,
                            mapOf("validator" to validatorName)))
                    } else {
                        validator.invoke(step, validatorParam, value) { s: String ->
                            ret.add(ValidateException(s, newKey))
                        }
                    }
                } else if (rule is AttributeValidatorInterface) {
                    // 回调形式的校验器

                    rule.invoke(step, "", value) { s: String -> ret.add(ValidateException(s, newKey)) }
                } else if (rule is Pair<*, *> && rule.first is String) {
                    // 键值对形式的校验器，键为校验器名

                    val validatorName = rule.first
                    val validatorParam = rule.second

                    val validator = validatorPool[validatorName]
                    if (validator == null) {
                        ret.add(ValidationInvalidException(ValidationInvalidException.Reason.VALIDATOR_NOT_FOUND,
                            mapOf("validator" to (validatorName ?: ""))))
                    } else {
                        validator.invoke(step, validatorParam, value) { s: String ->
                            ret.add(
                                ValidateException(s, newKey)
                            )
                        }
                    }
                } else if (rule is Pair<*, *> && rule.first is AttributeValidatorInterface) {
                    // 键值对形式的校验器，键为回调函数

                    (rule.first as AttributeValidatorInterface).invoke(step, rule.second, value) { s: String ->
                        ret.add(
                            ValidateException(s, newKey)
                        )
                    }
                }
            }
        } else {
            val value = content[step]
            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                return validateSingleParsedRecord(value as Map<String, Any?>, newKey, path.subList(1, path.size), rules)
            }
        }
        return ret
    }
}
