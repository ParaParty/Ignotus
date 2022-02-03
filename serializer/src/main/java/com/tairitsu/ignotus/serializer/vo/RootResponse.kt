package com.tairitsu.ignotus.serializer.vo

import com.tairitsu.ignotus.serializer.SerializerIgnore

class RootResponse {
    @SerializerIgnore
    internal var links: LinkedHashMap<String, String>? = null
        @SerializerIgnore get

    @SerializerIgnore
    internal var meta: LinkedHashMap<String, Any?>? = null
        @SerializerIgnore get

    internal var data: Any? = null
        @SerializerIgnore get

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

    /**
     * 设置结果数据
     */
    fun setData(data: BaseResponse) {
        this.data = data
    }

    /**
     * 设置结果数据
     */
    fun <T : BaseResponse> setData(data: Collection<T>) {
        this.data = data
    }

}
