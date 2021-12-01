package com.tairitsu.ignotus.validation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import com.tairitsu.ignotus.exception.ApiExceptionBag
import com.tairitsu.ignotus.exception.SingleApiException
import com.tairitsu.ignotus.exception.business.ValidateException
import com.tairitsu.ignotus.exception.business.ValidationInvalidException
import com.tairitsu.ignotus.support.util.Translation.lang
import com.tairitsu.ignotus.validation.annotation.Required
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField


@Component
@Lazy
class Validator {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    /**
     * 验证器合集
     */
    private lateinit var validatorPool: HashMap<String, AttributeValidatorInterface>

    /**
     * 验证器入口
     */
    fun validate(content: Any?, basePath: String = "") {
        ensureInitialized()

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
            val value = javaField.get(content)
            validateSingleField(javaField, value, basePath, exception)
        }
    }

    private fun validateSingleField(field: Field, value: Any?, key: String, exception: ArrayList<SingleApiException>) {
        val step = field.name
        val newKey = if (key == "") step else "$key.$step"

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
                    exception.add(ValidateException(lang("validation.required", mapOf("attribute" to step)), newKey))
                }
            } else if (value != null) {
                val name = annotationType.simpleName
                val validator =
                    validatorPool[name] ?: throw ValidationInvalidException("Validation rule \"$name\" is invalid.")

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


    /**
     * 验证器入口
     */
    fun <T : Any> validate(content: String?, validation: Map<String, Any>, basePath: String = "", type: KClass<T>): T {
        return validate(content, validation, basePath, type.java)
    }

    /**
     * 验证器入口
     */
    fun <T> validate(content: String?, validation: Map<String, Any>, basePath: String = "", type: Class<T>): T {
        if (content == null) {
            throw ValidationInvalidException("Null content")
        }
        val mapper = ObjectMapper()
        val typeRef = object : TypeReference<HashMap<String, Any?>>() {}
        val tree = mapper.readValue(content, typeRef)
        this.validate(tree, validation, basePath)
        return mapper.convertValue(tree, type)
    }

    /**
     * 验证器入口
     */
    fun validate(content: String?, validation: Map<String, Any>, basePath: String = "") {
        if (content == null) {
            throw ValidationInvalidException("Null content")
        }
        val mapper = ObjectMapper()
        val typeRef = object : TypeReference<HashMap<String, Any?>>() {}
        val tree = mapper.readValue(content, typeRef)
        this.validate(tree, validation, basePath)
    }

    /**
     * 验证器入口
     *
     * [content] 被验证的内容
     * [validation] 验证器 键为 json 路径，值为字符串或字符串数组或验证器实例。
     * 字符串格式为 "验证器1|验证器2:验证器2的参数"
     * 字符串数组格式为 ["验证器1"， "验证器2:验证器2的参数"]
     * 验证器实例
     *
     * [basePath] 报错的根
     * 如传递 [basePath] = "test" ，[validation] 的某个键为 foo，若验证失败报错的路径为 "test.foo"
     *
     * @throw ApiExceptionBag
     */
    fun validate(content: Map<String, Any?>, validation: Map<String, Any>, basePath: String = "") {
        ensureInitialized()

        validation.forEach { (k, v) ->
            if (
                ((v !is String) && (v !is Collection<*>) && (v !is AttributeValidatorInterface) && (v !is Pair<*, *>))
            ) {
                throw ValidationInvalidException("Validation rule \"$k\" is invalid.")
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

    private fun ensureInitialized() {
        if (this::validatorPool.isInitialized) return

        validatorPool = HashMap()

        val beans: Map<String, AttributeValidatorInterface> =
            applicationContext.getBeansOfType(AttributeValidatorInterface::class.java)

        beans.forEach { (k, v) ->
            val name = if (k.startsWith("validator") || k.startsWith("Validator")) {
                k.substring("validator".length)
            } else {
                k
            }

            if (validatorPool[name] != null) {
                throw ValidationInvalidException("Validation rule \"$k\" duplicated.")
            }
            validatorPool[name] = v
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
            else -> throw ValidationInvalidException("Validation rule \"$key\" : \"$rule\" is invalid.")
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
        val newKey = if (key == "") step else "$key.$step"

        if (!content.containsKey(step)) {
            rules.forEach { rule ->
                if (rule is String) {
                    val t = rule.split(Regex(":"), 1)
                    val validatorName = t[0]
                    if (validatorName == "required") {
                        ret.add(ValidateException("$step is required", newKey))
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
                        ret.add(ValidationInvalidException("Validator \"$validatorName\" does not exists"))
                    } else {
                        validator.invoke(step, validatorParam, value) { s: String ->
                            ret.add(
                                ValidateException(
                                    s,
                                    newKey
                                )
                            )
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
                        ret.add(ValidationInvalidException("Validator \"$validatorName\" does not exists"))
                    } else {
                        validator.invoke(step, validatorParam, value) { s: String ->
                            ret.add(
                                ValidateException(
                                    s,
                                    newKey
                                )
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
