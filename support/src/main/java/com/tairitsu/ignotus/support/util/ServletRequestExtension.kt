package com.tairitsu.ignotus.support.util

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
    fun ServletRequest.getExtractedSort(): Sort? =
        this.getAttribute("extracted_sort") as Sort?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedSort(value: Sort) =
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
    fun ServletRequest.getExtractedPagination(): Pageable? =
        this.getAttribute("extracted_pagination") as Pageable?

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun ServletRequest.setExtractedPagination(value: Pageable) =
        this.setAttribute("extracted_pagination", value)
}
