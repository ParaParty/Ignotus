package com.tairitsu.ignotus.serializer.vo

import com.tairitsu.ignotus.exception.relation.RelationshipInvalidException
import com.tairitsu.ignotus.serializer.DefaultSerializer
import com.tairitsu.ignotus.serializer.Serializer
import com.tairitsu.ignotus.serializer.SerializerIgnore
import com.tairitsu.ignotus.support.util.toRelatedFunction
import kotlin.reflect.KClass

/**
 * 实体类基本定义
 *
 * 在本项目中所有的需要被序列化为客户端可见的信息的实体类均从本类继承。
 */
abstract class BaseResponse {
    /**
     * JSON:API 规范中 `type` 字段
     */
    @SerializerIgnore
    abstract val modelType: String
        @SerializerIgnore get

    /**
     * 本类型的 JSON:API 规范中的 `attributes` 字段的序列化类。
     */
    @SerializerIgnore
    var modelSerializer: Any = DefaultSerializer.defaultSerializer
        @SerializerIgnore private set

    /**
     * 本类型的 JSON:API 规范中的 `attributes` 字段的序列化类。
     */
    fun setModelSerializer(serializer: Serializer<out BaseResponse>) {
        this.modelSerializer = serializer
    }

    /**
     * 本类型的 JSON:API 规范中的 `attributes` 字段的序列化类。
     */
    fun setModelSerializer(serializer: Class<out Serializer<out BaseResponse>>) {
        this.modelSerializer = serializer
    }

    /**
     * 本类型的 JSON:API 规范中的 `attributes` 字段的序列化类。
     */
    fun setModelSerializer(serializer: KClass<out Serializer<out BaseResponse>>) {
        this.modelSerializer = serializer.java
    }

    /**
     * JSON:API 规范中 `id` 字段
     */
    @SerializerIgnore
    abstract val id: String
        @SerializerIgnore get

    /**
     * JSON:API 规范中 `relationship` 字段
     */
    @SerializerIgnore
    internal val relationships = LinkedHashMap<String, Any?>()
        @SerializerIgnore get

    /**
     * JSON:API 规范中 `links` 字段
     */
    @SerializerIgnore
    internal var links: LinkedHashMap<String, String>? = null
        @SerializerIgnore get

    /**
     * JSON:API 规范中 `meta` 字段
     */
    @SerializerIgnore
    internal var meta: LinkedHashMap<String, Any?>? = null
        @SerializerIgnore get

    /**
     * 给当前对象添加一个关系
     *
     * @param [key] 为欲添加关系的关系名
     * @param [value] 为欲添加关系的被关联对象
     */
    fun setRelationship(key: String, value: BaseResponse?) {
        this.relationships[key] = value
    }

    /**
     * 给当前对象添加一个关系
     *
     * @param [key] 为欲添加关系的关系名
     * @param [value] 为欲添加关系的被关联对象
     */
    fun setRelationship(key: String, value: Collection<BaseResponse>?) {
        this.relationships[key] = value
    }

    /**
     * 给当前对象添加一个关系
     *
     * @param [key] 为欲添加关系的关系名
     * @param [value] 为欲添加关系的被关联对象，该值必须为 `null`， [BaseResponse] 或 [BaseResponse] 的一个 [Collection]。
     */
    fun setRelationship(key: String, value: Any?) {
        if (value is BaseResponse) {
            this.relationships[key] = value
        } else if (value is Collection<*>) {
            if (value.isEmpty()) {
                this.relationships[key] = setOf<BaseResponse>()
            } else {
                val t = value.first()
                if (t is BaseResponse) {
                    this.relationships[key] = value
                } else {
                    throw RelationshipInvalidException(
                        RelationshipInvalidException.Reason.INVALID_RELATED_COLLECTION, mapOf(
                            "type" to this.modelType,
                            "key" to key,
                        )
                    )
                }
            }
        } else if (value == null) {
            this.relationships[key] = null
        } else {
            throw RelationshipInvalidException(
                RelationshipInvalidException.Reason.INVALID_RELATED_OBJECT, mapOf(
                    "type" to this.modelType,
                    "key" to key,
                )
            )
        }
    }

    /**
     * 获取当前对象的一个关系
     *
     * @param [key] 为欲获取关系的关系名
     * @return 返回这个关系的被关联对象，值可能为 `null`、[BaseResponse] 或 [BaseResponse] 的一个 [Collection]。
     */
    fun getRelationship(key: String): Any? {
        return this.relationships[key]
    }

    /**
     * 给当前对象载入一个关系
     *
     * @param [relation] 为欲载入关系的关系名，请确认在当前对象存在名为 `"related" + relation` 的方法，
     * 并且该方法的返回值为 `null`， [BaseResponse] 或 [BaseResponse] 的一个 [Collection]。
     * 若不存在，则报 [RelationshipInvalidException] 异常。
     * 如： [relation] = friends 则要存在一个名为 getFriends 的方法
     * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
     */
    fun loadRelationship(relation: String, overwrite: Boolean = false) {
        val path = relation.split('.')

        if (path.isEmpty()) {
            throw RelationshipInvalidException(
                RelationshipInvalidException.Reason.EMPTY_RELATIONSHIP_NAME,
                mapOf("type" to this.modelType)
            )
        }

        val aRelation = path[0]
        if (this.relationships[aRelation] == null || overwrite) {
            val type = this.javaClass

            val functionName: String = aRelation.toRelatedFunction()

            try {
                val aMethod = type.getMethod(functionName)
                val value = aMethod.invoke(this)
                this.setRelationship(aRelation, value)
            } catch (e: NoSuchMethodException) {
                throw RelationshipInvalidException(
                    RelationshipInvalidException.Reason.RELATIONSHIP_METHOD_NOT_FOUND, mapOf(
                        "class" to type.name,
                        "method" to functionName,
                        "type" to modelType,
                        "relation" to relation
                    )
                )
            } catch (e: SecurityException) {
                throw RelationshipInvalidException(
                    RelationshipInvalidException.Reason.RELATIONSHIP_METHOD_NOT_FOUND, mapOf(
                        "class" to type.name,
                        "method" to functionName,
                        "type" to modelType,
                        "relation" to relation
                    )
                )
            }
        }

        if (path.size > 1) {
            val next = path.subList(1, path.size).joinToString(separator = ".")
            val value = this.relationships[aRelation]

            if (value is BaseResponse) {
                value.loadRelationship(next, overwrite)
            } else if (value is Collection<*> && value.isNotEmpty()) {
                value.forEach { s ->
                    val v = s as BaseResponse
                    v.loadRelationship(next, overwrite)
                }
            }
        }
    }

    /**
     * 给当前对象载入一组关系
     *
     * @param [relations] 为欲载入关系的关系名，具体介绍见 [BaseResponse.loadRelationship]
     * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
     */
    fun loadRelationship(relations: Set<String>, overwrite: Boolean = false) {
        relations.forEach { s ->
            this.loadRelationship(s, overwrite)
        }
    }

    /**
     * 给当前对象设置一个链接（请保证本函数不被并发）
     */
    fun setLink(key: String, value: String) {
        if (this.links == null) {
            this.links = LinkedHashMap()
        }

        this.links!![key] = value
    }

    /**
     * 获取当前对象的一个链接
     */
    fun getLink(key: String): String? {
        return this.links?.get(key)
    }

    /**
     * 给当前对象设置一个元信息（请保证本函数不被并发）
     */
    fun setMeta(key: String, value: Any?) {
        if (this.meta == null) {
            this.meta = LinkedHashMap()
        }

        this.meta!![key] = value
    }

    /**
     * 获取当前对象的一个元信息
     */
    fun getMeta(key: String): Any? {
        return this.meta?.get(key)
    }

    companion object {
        /**
         * 给目标对象载入一组关系
         *
         * @param [model] 目标对象
         * @param [relations] 为欲载入关系的关系名，具体介绍见 [BaseResponse.loadRelationship]
         * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
         */
        fun loadRelationship(
            model: BaseResponse,
            relations: Set<String>,
            overwrite: Boolean = false,
        ) {
            relations.forEach { s ->
                model.loadRelationship(s, overwrite)
            }
        }

        /**
         * 给目标对象载入一个关系
         *
         * @param [model] 目标对象
         * @param [relation] 为欲载入关系的关系名，具体介绍见 [BaseResponse.loadRelationship]
         * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
         */
        fun loadRelationship(
            model: BaseResponse,
            relation: String,
            overwrite: Boolean = false,
        ) {
            model.loadRelationship(relation, overwrite)
        }

        /**
         * 给目标对象载入一组关系
         *
         * @param [model] 目标对象
         * @param [relations] 为欲载入关系的关系名，具体介绍见 [BaseResponse.loadRelationship]
         * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
         */
        fun loadRelationship(model: Collection<*>, relations: Set<String>, overwrite: Boolean = false) {
            relations.forEach { s ->
                loadRelationship(model, s, overwrite)
            }
        }

        /**
         * 给目标对象载入一个关系
         *
         * @param [model] 目标对象
         * @param [relation] 为欲载入关系的关系名，具体介绍见 [BaseResponse.loadRelationship]
         * @param [overwrite] 为若该关系已被载入，是否覆盖掉原有的关系信息。一般取 `false`。
         */
        fun loadRelationship(model: Collection<*>, relation: String, overwrite: Boolean = false) {
            model.forEach { c ->
                val s = c as BaseResponse
                s.loadRelationship(relation, overwrite)
            }
        }
    }
}
