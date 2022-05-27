package com.tairitsu.ignotus.serializer

import com.tairitsu.ignotus.serializer.vo.BaseResponse
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import com.tairitsu.ignotus.support.util.toGetterFunction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.util.LinkedHashMap
import kotlin.reflect.KProperty1
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaGetter

class DefaultSerializer<T : BaseResponse> : Serializer<T> {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * JSON:API 规范中的 `attributes` 字段的序列化方法。
     */
    override fun defaultAttributeSerialize(model: T): Map<String, Any?> {
        val ret = LinkedHashMap<String, Any?>()

        val type = model::class
        val fields = type.declaredMemberProperties

        for (field in fields) {
            val name = field.name
            val ignoreMode = checkIgnoreMode(field)
            if (ignoreMode == IgnoreMode.FILTER) continue

//            val javaField = field.javaField ?: continue
//            field.isAccessible = true
//            javaField.isAccessible = true

            val outputName = JacksonNamingStrategyConfig.namingStrategy?.nameForField(null, null, name) ?: name

            var done = false
            try {
                val value = field.getter.call(model)
                if (value == null && ignoreMode == IgnoreMode.OMITNULL) {
                    continue;
                }
                ret[outputName] = value
                done = true
            } catch (_: InvocationTargetException) {

            } catch (_: IllegalAccessException) {

            } catch (_: IllegalCallableAccessException) {

            } catch (e: Exception) {
                log.error(e.message, e)
            }

            if (done) {
                continue
            }

            try {
                val getMethod = field.javaGetter ?: type.java.getMethod(name.toGetterFunction())
                val value = getMethod.invoke(model)
                if (value == null && ignoreMode == IgnoreMode.OMITNULL) {
                    continue;
                }
                ret[outputName] = value
//                done = true
            } catch (_: InvocationTargetException) {

            } catch (_: UninitializedPropertyAccessException) {

            } catch (_: NoSuchMethodException) {

            } catch (_: SecurityException) {

            } catch (_: SecurityException) {

            } catch (e: Exception) {
                log.error(e.message, e)
            }
        }

        return ret
    }

    enum class IgnoreMode { NONE, OMITNULL, FILTER }

    private fun checkIgnoreMode(field: KProperty1<out T, *>): IgnoreMode {
        if (field.name in Serializer.preservedFields) {
            return IgnoreMode.FILTER
        }

        val annotations = field.annotations
        for (annotation in annotations) {
            val proxiedAnnotationType = annotation.javaClass
            val proxiedAnnotationMethods = proxiedAnnotationType.declaredMethods.associateBy { it.name }
            val annotationType =
                (proxiedAnnotationMethods["annotationType"]?.invoke(annotation) ?: continue) as Class<*>
            if (annotationType == SerializerIgnore::class.java) {
                return IgnoreMode.FILTER
            }
            if (annotationType == OmitNull::class.java) {
                return IgnoreMode.OMITNULL
            }
        }
        return IgnoreMode.NONE
    }

    companion object {
        val defaultSerializer = DefaultSerializer<BaseResponse>()
    }
}
