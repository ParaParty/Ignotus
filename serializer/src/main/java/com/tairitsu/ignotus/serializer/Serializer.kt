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
interface Serializer<T : BaseResponse> {

    operator fun invoke(model: T) = defaultAttributeSerialize(model)

    fun defaultAttributeSerialize(model: T): Map<String, Any?>

    companion object {
        val preservedFields = setOf("modelType", "modelSerializer", "id", "relationships")

        /**
         * 序列化 REST API 的结果
         */
        fun serializeTopLevel(data: Any, request: HttpServletRequest): Map<String, Any> {
            return when (data) {
                is BaseResponse -> serializeTopLevelResourceSingleObject(
                    data,
                    request.getAttribute("json-api_links"),
                    request.getAttribute("json-api_meta"),
                )
                is Collection<*> -> serializeTopLevelResourceObjectsList(
                    data,
                    request.getAttribute("json-api_links"),
                    request.getAttribute("json-api_meta"),
                )
                is RootResponse -> {
                    data.data?.let { dataCopy ->
                        when (dataCopy) {
                            is BaseResponse -> serializeTopLevelResourceSingleObject(
                                dataCopy,
                                data.links,
                                data.meta,
                            )
                            is Collection<*> -> serializeTopLevelResourceObjectsList(
                                dataCopy,
                                data.links,
                                data.meta,
                            )
                            else -> throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
                        }
                    } ?: serializeTopLevelResourceObjectsList(
                        Collections.EMPTY_LIST,
                        data.links,
                        data.meta,
                    )
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
        ): Map<String, Any> {
            // 处理列表信息
            val data = ArrayList<Any>()
            resources.forEach { c ->
                val s = c as BaseResponse
                data.add(serializeSingleResourceObject(s))
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
            includedPool.forEach { (_, v) -> included.add(serializeSingleResourceObject(v)) }

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
        ): Map<String, Any> {
            // 单条记录信息
            val data = serializeSingleResourceObject(model)

            // 处理后的关联信息
            val includedPool = LinkedHashMap<String, BaseResponse>()

            // 待处理的关联信息
            val pendingModels = ArrayList<BaseResponse>()
            addToPendingResources(model, pendingModels)

            // 处理关联信息
            processIncludedRelationship(pendingModels, includedPool)
            @Suppress("MemberVisibilityCanBePrivate", "MemberVisibilityCanBePrivate") val included = ArrayList<Any>()
            includedPool.forEach { (_, v) -> included.add(serializeSingleResourceObject(v)) }

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
        ): Map<String, Any> {
            val data = LinkedHashMap<String, Any>()

            val serializerType = model.modelSerializer
            @Suppress("UNCHECKED_CAST")
            val serializerInstance = if (serializerType is Serializer<*>) {
                serializerType as Serializer<BaseResponse>
            } else if (serializerType == Serializer::class.java || serializerType == DefaultSerializer::class.java) {
                DefaultSerializer.defaultSerializer
            } else if (serializerType is Class<*>) {
                serializerType.getDeclaredConstructor().newInstance() as Serializer<BaseResponse>
            } else {
                throw SerializerException(SerializerException.Reason.API_RESULT_UNACCEPTABLE_TYPE)
            }

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
