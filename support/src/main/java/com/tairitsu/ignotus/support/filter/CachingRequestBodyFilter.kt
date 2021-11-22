package com.tairitsu.ignotus.support.filter

import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean
import org.springframework.web.util.ContentCachingRequestWrapper
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Component
class CachingRequestBodyFilter : GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val currentRequest = servletRequest as HttpServletRequest
        val wrappedRequest = ContentCachingRequestWrapper(currentRequest)
        chain.doFilter(wrappedRequest, servletResponse)
    }
}
