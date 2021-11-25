package com.tairitsu.ignotus.support.model.vo

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.io.Serializable

/**
 * 基于偏移量的分页对象
 */
class OffsetBasedPagination : Pageable, Serializable {
    private var offset: Long = 0
    private var limit: Int = 20
    private var sort: Sort = Sort.unsorted()

    constructor(offset: Long, limit: Int, sort: Sort) {
        this.offset = offset
        this.limit = limit
        this.sort = sort
    }

    constructor(offset: Long, limit: Int) {
        this.offset = offset
        this.limit = limit
        this.sort = Sort.unsorted()
    }

    constructor(offset: Int, limit: Int, sort: Sort) {
        this.offset = offset.toLong()
        this.limit = limit
        this.sort = sort
    }

    constructor(offset: Int, limit: Int) {
        this.offset = offset.toLong()
        this.limit = limit
        this.sort = Sort.unsorted()
    }

    override fun getPageNumber() = (offset / limit).toInt()

    override fun getPageSize() = limit

    override fun getOffset() = offset

    override fun getSort() = sort

    override fun next() = OffsetBasedPagination(offset + limit, limit, sort)

    fun previous() = if (hasPrevious()) OffsetBasedPagination(offset - limit, limit, sort) else this

    override fun previousOrFirst() = if (hasPrevious()) previous() else first()

    override fun first() = OffsetBasedPagination(0, limit, sort)

    override fun withPage(pageNumber: Int) = PageRequest.of(pageNumber, limit, sort)

    override fun hasPrevious() = offset > limit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OffsetBasedPagination

        if (offset != other.offset) return false
        if (limit != other.limit) return false
        if (sort != other.sort) return false

        return true
    }

    override fun hashCode(): Int {
        var ret = limit.hashCode()
        ret = ret * 31 + offset.hashCode()
        ret = ret * 31 + sort.hashCode()
        return ret
    }

    override fun toString(): String {
        return "limit:${limit}, offset:${offset}, sort:${sort}"
    }
}
