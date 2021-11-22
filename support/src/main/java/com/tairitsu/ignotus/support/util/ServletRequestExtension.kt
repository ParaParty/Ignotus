package com.tairitsu.ignotus.support.util

import com.tairitsu.ignotus.support.model.vo.Pagination
import javax.servlet.ServletRequest

class ServletRequestAttribute(private val self: ServletRequest) {
    operator fun get(key: String): Any? = self.getAttribute(key)

    operator fun set(key: String, value: Any?) = self.setAttribute(key, value)
}

object ServletRequestExtension {
    @JvmStatic
    fun ServletRequest.attribute() = ServletRequestAttribute(this)

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.getExtractedInclude(): Set<String>? = this.getAttribute("extracted_include") as Set<String>?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedInclude(value: Set<String>) = this.setAttribute("extracted_include", value)

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.getExtractedSort(): ArrayList<Pair<String, String>>? =
        this.getAttribute("extracted_sort") as ArrayList<Pair<String, String>>?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedSort(value: ArrayList<Pair<String, String>>) =
        this.setAttribute("extracted_sort", value)

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.getExtractedFilter(): HashMap<String, String>? =
        this.getAttribute("extracted_filter") as HashMap<String, String>?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedFilter(value: HashMap<String, String>) =
        this.setAttribute("extracted_filter", value)

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.getExtractedPagination(): Pagination? =
        this.getAttribute("extracted_pagination") as Pagination?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedPagination(value: Pagination) =
        this.setAttribute("extracted_pagination", value)
}
