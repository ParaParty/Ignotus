package com.tairitsu.ignotus.exception

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Config {
    @Value("\${ignotus.exception.debug:false}")
    private var debug: Boolean = false
        set(s) {
            IS_DEBUG = s
            field = s
        }
        get() = IS_DEBUG

    companion object {
        var IS_DEBUG = false
    }
}
