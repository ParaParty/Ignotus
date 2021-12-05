package com.tairitsu.ignotus.support

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * 全局 Spring Application 获取器
 */
@Component("IgnotusApplicationContextRegister")
class ApplicationContextRegister : ApplicationContextAware {

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext?) {
        APPLICATION_CONTEXT = applicationContext
    }

    companion object {
        private var APPLICATION_CONTEXT: ApplicationContext? = null

        @JvmStatic
        val applicationContext: ApplicationContext? get() = APPLICATION_CONTEXT
    }
}
