package com.tairitsu.ignotus.exception

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component("IgnotusExceptionConfig")
@ConfigurationProperties(prefix = "ignotus.exception")
open class ExceptionConfig {
    @JvmField var debug: Boolean = false

    open fun isDebug(): Boolean {
        return debug
    }

    open fun setDebug(debug: Boolean) {
        IS_DEBUG = debug
        this.debug = debug
    }

    companion object {
        var IS_DEBUG = false
    }
}
