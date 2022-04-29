package com.tairitsu.ignotus.serializer

import com.tairitsu.ignotus.exception.serialize.SerializerException
import com.tairitsu.ignotus.serializer.vo.BaseResponse
import com.tairitsu.ignotus.serializer.vo.RootResponse
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import com.tairitsu.ignotus.support.util.toGetterFunction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.util.*
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KProperty1
import kotlin.reflect.full.IllegalCallableAccessException
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaGetter


/**
 * 实体类的序列化类基本定义
 */
open class Serializer<T : BaseResponse> {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 关联请求
     *
     * 本次序列化是来自哪个请求的
     */
    open lateinit var request: HttpServletRequest

    /**
     * JSON:API 规范中的 `attributes` 字段的序列化方法。
     */
    open fun defaultAttributeSerialize(model: T): Map<String, Any?> {
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

            } catch (e: Exception){
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

    enum IgnoreMode{ NONE, OMITNULL, FILTER}
    private fun checkIgnoreMode(field: KProperty1<out T, *>): IgnoreMode {
        if (field.name in preservedFields) {
            return true
        }

        val annotations = field.annotations
        for (annotation in annotations) {
            val proxiedAnnotationType = annotation.javaClass
            val proxiedAnnotationMethods = proxiedAnnotationType.declaredMethods.associateBy { it.name }
            val annotationType = (proxiedAnnotationMethods["annotationType"]?.invoke(annotation) ?: continue) as Class<*>
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
        val preservedFields = setOf("modelType", "modelSerializer", "id", "relationships")

        /**
         * 序列化 REST API 的结果
         */
        fun serializeTopLevel(data: Any, request: HttpServletRequest): Map<String, Any> {
            return when (data) {
                is BaseResponse -> serializeTopLevelResourceSingleObject(data,
                    request.getAttribute("json-api_links"),
                    request.getAttribute("json-api_meta"),
                    request)
                is Collection<*> -> serializeTopLevelResourceObjectsList(data,
                    request.getAttribute("json-api_links"),
                    request.getAttribute("json-api_meta"),
                    request)
                is RootResponse -> {
                    data.data?.let { dataCopy ->
                        when (dataCopy) {
                            is BaseResponse -> serializeTopLevelResourceSingleObject(dataCopy,
                                data.links,
                                data.meta,
                                request)
                            is Collection<*> -> serializeTopLevelResourceObjectsList(dataCopy,
                                data.links,
                                data.meta,
                                request)
                            else -> throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
                        }
                    } ?: serializeTopLevelResourceObjectsList(Collections.EMPTY_LIST,
                        data.links,
                        data.meta,
                        request)
                }
                else -> throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
            }
        }

        /**
         * 序列化列表类 REST API 的结果
         * 列表记录 顶层 data 是 list 的情况
         */
        @Suppress("DuplicatedCode")
        private fun serializeTopLevelResourceObjectsList(
            resources: Collection<*>,
            links: Any?,
            meta: Any?,
            request: HttpServletRequest,
        ): Map<String, Any> {
            // 处理列表信息
            val data = ArrayList<Any>()
            resources.forEach { c ->
                val s = c as BaseResponse
                data.add(serializeSingleResourceObject(s, request))
            }

            // 处理后的关联信息
            val includedPool = LinkedHashMap<String, BaseResponse>()

            // 待处理的关联信息
            val pendingModels = ArrayList<BaseResponse>()
            resources.forEach { c ->
                val s = c as BaseResponse
                addToPendingResources(s, pendingModels)
            }

            // 处理关联信息
            processIncludedRelationship(pendingModels, includedPool)
            val included = ArrayList<Any>()
            includedPool.forEach { (_, v) -> included.add(serializeSingleResourceObject(v, request)) }

            // 拼接最终的结果
            val ret = LinkedHashMap<String, Any>()
            if (links is Map<*, *>) ret["links"] = links
            if (meta is Map<*, *>) ret["meta"] = meta
            ret["data"] = data
            if (included.size > 0) ret["included"] = included
            return ret
        }

        /**
         * 序列化资源类 REST API 的结果
         * 单条记录 顶层 data 是 object 的情况
         */
        @Suppress("DuplicatedCode")
        private fun serializeTopLevelResourceSingleObject(
            model: BaseResponse,
            links: Any?,
            meta: Any?,
            request: HttpServletRequest,
        ): Map<String, Any> {
            // 单条记录信息
            val data = serializeSingleResourceObject(model, request)

            // 处理后的关联信息
            val includedPool = LinkedHashMap<String, BaseResponse>()

            // 待处理的关联信息
            val pendingModels = ArrayList<BaseResponse>()
            addToPendingResources(model, pendingModels)

            // 处理关联信息
            processIncludedRelationship(pendingModels, includedPool)
            @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate") val included = ArrayList<Any>()
            includedPool.forEach { (_, v) -> included.add(serializeSingleResourceObject(v, request)) }

            // 拼接最终的结果
            val ret = LinkedHashMap<String, Any>()
            if (links is Map<*, *>) ret["links"] = links
            if (meta is Map<*, *>) ret["meta"] = meta
            ret["data"] = data
            if (included.size > 0) ret["included"] = included
            return ret
        }

        /**
         * 关联信息处理方法
         */
        private fun processIncludedRelationship(
            pendingModels: ArrayList<BaseResponse>,
            includedPool: MutableMap<String, BaseResponse>,
        ) {
            while (pendingModels.isNotEmpty()) {
                val s = pendingModels.first()
                pendingModels.remove(s)

                val poolObject = includedPool["${s.modelType}.${s.id}"]
                if (poolObject == null) {
                    includedPool["${s.modelType}.${s.id}"] = s
                } else {
                    s.relationships.forEach { (k, v) ->
                        val t = poolObject.relationships[k]
                        if (t == null) {
                            poolObject.setRelationship(k, v)
                        } else {
                            // ignore
                            // 假定在一个请求中对于某个对象的关系的返回结果都是一样的
                        }
                    }
                }

                addToPendingResources(s, pendingModels)
            }
        }

        /**
         * 添加关联处理队列
         */
        private fun addToPendingResources(
            model: BaseResponse,
            pendingModels: ArrayList<BaseResponse>,
        ) {
            model.relationships.forEach { (_, v) ->
                @Suppress("ControlFlowWithEmptyBody", "CascadeIf")
                if (v is BaseResponse) {
                    pendingModels.add(v)
                } else if (v is Collection<*>) {
                    v.forEach { c ->
                        val s = c as BaseResponse
                        pendingModels.add(s)
                    }
                } else if (v == null) {

                } else {
                    throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
                }
            }
        }

        /**
         * 序列化一个对象
         */
        private fun serializeSingleResourceObject(
            model: BaseResponse,
            request: HttpServletRequest,
        ): Map<String, Any> {
            val data = LinkedHashMap<String, Any>()

            val serializerType = model.modelSerializer
            @Suppress("UNCHECKED_CAST")
            val serializerInstance = if (serializerType == Serializer::class.java) {
                Serializer<BaseResponse>()
            } else {
                serializerType.getDeclaredConstructor().newInstance() as Serializer<BaseResponse>
            }
            serializerInstance.request = request

            val dataResult = serializerInstance.defaultAttributeSerialize(model)
            data["type"] = model.modelType
            data["id"] = model.id
            model.links?.let { data["links"] = it }
            model.meta?.let { data["meta"] = it }
            data["attributes"] = dataResult

            if (model.relationships.isNotEmpty()) {
                val relationships = LinkedHashMap<String, Any>()
                model.relationships.forEach { (k, v) ->
                    @Suppress("CascadeIf")
                    if (v is BaseResponse) {
                        val tData = LinkedHashMap<String, Any>()
                        tData["type"] = v.modelType
                        tData["id"] = v.id

                        val tRelation = LinkedHashMap<String, Any>()
                        tRelation["data"] = tData

                        relationships[k] = tRelation
                    } else if (v is Collection<*>) {
                        val aData = ArrayList<Any>()

                        v.forEach { c ->
                            val s = c as BaseResponse
                            val tData = LinkedHashMap<String, Any>()
                            tData["type"] = s.modelType
                            tData["id"] = s.id

                            aData.add(tData)
                        }

                        val tRelation = LinkedHashMap<String, Any>()
                        tRelation["data"] = aData
                        relationships[k] = aData
                    } else if (v == null) {
                        val tRelation = LinkedHashMap<String, Any?>()
                        tRelation["data"] = null

                        relationships[k] = tRelation
                    } else {
                        throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
                    }
                }
                data["relationships"] = relationships
            }

            return data
        }
    }
}
