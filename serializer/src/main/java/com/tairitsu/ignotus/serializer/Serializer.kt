package com.tairitsu.ignotus.serializer

import com.tairitsu.ignotus.exception.serialize.SerializerException
import com.tairitsu.ignotus.serializer.vo.BaseResponse
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import com.tairitsu.ignotus.support.util.toGetterFunction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
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
        val ret = mutableMapOf<String, Any?>()

        val type = model::class
        val fields = type.declaredMemberProperties

        for (field in fields) {
            val name = field.name
            if (preservedFields.contains(name)) continue

            val javaField = field.javaField ?: continue
            field.isAccessible = true
            javaField.isAccessible = true

            val outputName = JacksonNamingStrategyConfig.namingStrategy?.nameForField(null, null, name) ?: name

            var done = false
            try {
                val value = field.getter.call(model)
                ret[outputName] = value
                done = true
            } catch (_: InvocationTargetException) {

            } catch (_: IllegalAccessException) {

            } catch (e: Exception){
                log.error(e.message, e)
            }

            if (done) {
                continue
            }

            try {
                val getMethod = field.javaGetter ?: type.java.getMethod(name.toGetterFunction())
                val value = getMethod.invoke(model)
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

    companion object {
        val preservedFields = setOf("modelType", "modelSerializer", "id", "relationships")

        /**
         * 序列化 REST API 的结果
         */
        fun serialize(data: Any, request: HttpServletRequest): Map<String, Any> {
            return when (data) {
                is BaseResponse -> serializeEntity(data, request)
                is Collection<*> -> serializeEntities(data, request.getAttribute("json-api_links"), request)
                else -> throw SerializerException("Api result is not a BaseResponse or a set of BaseResponses.")
            }
        }

        /**
         * 序列化列表类 REST API 的结果
         * 列表记录
         */
        private fun serializeEntities(
            models: Collection<*>,
            links: Any?,
            request: HttpServletRequest,
        ): Map<String, Any> {
            // 单条记录信息
            val data = ArrayList<Any>()
            models.forEach { c ->
                val s = c as BaseResponse
                data.add(serializeSingleEntity(s, request))
            }

            // 处理后的关联信息
            val includedPool = mutableMapOf<String, BaseResponse>()

            // 待处理的关联信息
            val pendingModels = ArrayList<BaseResponse>()
            models.forEach { c ->
                val s = c as BaseResponse
                addToPendingModels(s, pendingModels)
            }

            // 处理关联信息
            processIncludedRelationship(pendingModels, includedPool)
            val included = ArrayList<Any>()
            includedPool.forEach { (_, v) -> included.add(serializeSingleEntity(v, request)) }

            // 拼接最终的结果
            val ret = HashMap<String, Any>()
            ret.put("data", data)
            if (included.size > 0) ret.put("included", included)
            if (links is Map<*, *>) ret.put("links", links) else ret.put("links", mapOf<String, String>())
            return ret
        }

        /**
         * 序列化资源类 REST API 的结果
         * 单条记录
         */
        private fun serializeEntity(
            model: BaseResponse,
            request: HttpServletRequest,
        ): Map<String, Any> {
            // 单条记录信息
            val data = serializeSingleEntity(model, request)

            // 处理后的关联信息
            val includedPool = mutableMapOf<String, BaseResponse>()

            // 待处理的关联信息
            val pendingModels = ArrayList<BaseResponse>()
            addToPendingModels(model, pendingModels)

            // 处理关联信息
            processIncludedRelationship(pendingModels, includedPool)
            val included = ArrayList<Any>()
            includedPool.forEach { (_, v) -> included.add(serializeSingleEntity(v, request)) }

            // 拼接最终的结果
            val ret = HashMap<String, Any>()
            ret.put("data", data)
            if (included.size > 0) ret.put("included", included)
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
                    s.relationships.forEach(fun(k, v) {
                        val t = poolObject.relationships[k]
                        if (t == null) {
                            poolObject.setRelationship(k, v)
                        } else {
                            // ignore
                            // 假定在一个请求中对于某个对象的关系的返回结果都是一样的
                        }
                    })
                }

                addToPendingModels(s, pendingModels)
            }
        }

        /**
         * 添加关联处理队列
         */
        private fun addToPendingModels(
            model: BaseResponse,
            pendingModels: ArrayList<BaseResponse>,
        ) {
            model.relationships.forEach(fun(_, v) {
                if (v is BaseResponse) {
                    pendingModels.add(v)
                } else if (v is Collection<*>) {
                    v.forEach(fun(c) {
                        val s = c as BaseResponse
                        pendingModels.add(s)
                    })
                } else if (v == null) {

                } else {
                    throw SerializerException("The related object is not a BaseResponse or a set of BaseResponses.")
                }
            })
        }

        val defaultSerializer = Serializer<BaseResponse>();

        /**
         * 序列化一个对象
         */
        private fun serializeSingleEntity(
            model: BaseResponse,
            request: HttpServletRequest,
        ): HashMap<String, Any> {
            val data = HashMap<String, Any>()

            val serializerType = model.modelSerializer

            @Suppress("UNCHECKED_CAST")
            val serializerInstance = if (serializerType == Serializer::class.java) {
                defaultSerializer
            } else {
                serializerType.getDeclaredConstructor().newInstance() as Serializer<BaseResponse>
            }
            serializerInstance.request = request

            val dataResult = serializerInstance.defaultAttributeSerialize(model)
            data.put("type", model.modelType)
            data.put("id", model.id)
            data.put("attributes", dataResult)

            if (model.relationships.isNotEmpty()) {
                val relationships = HashMap<String, Any>()
                model.relationships.forEach { (k, v) ->
                    if (v is BaseResponse) {
                        val tData = HashMap<String, Any>()
                        tData.put("type", v.modelType)
                        tData.put("id", v.id)

                        val tRelation = HashMap<String, Any>()
                        tRelation.put("data", tData)

                        relationships.put(k, tRelation)
                    } else if (v is Collection<*>) {
                        val aData = ArrayList<Any>()

                        v.forEach(fun(c) {
                            val s = c as BaseResponse
                            val tData = HashMap<String, Any>()
                            tData.put("type", s.modelType)
                            tData.put("id", s.id)

                            aData.add(tData)
                        })

                        val tRelation = HashMap<String, Any>()
                        tRelation.put("data", aData)

                        relationships.put(k, aData)
                    } else if (v == null) {
                        val tRelation = HashMap<String, Any?>()
                        tRelation.put("data", null)

                        relationships.put(k, tRelation)
                    } else {
                        throw SerializerException("The related object is not a BaseResponse or a set of BaseResponses.")
                    }
                }
                data.put("relationships", relationships)
            }

            return data
        }
    }
}
