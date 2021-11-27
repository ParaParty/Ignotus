package com.tairitsu.ignotus.foundation

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component("IgnotusFoundationConfig")
@ConfigurationProperties(prefix = "ignotus.foundation")
open class FoundationConfig {
    var pagination: FoundationPaginationConfig = FoundationPaginationConfig()
        set(s) {
            if (s.minLimit > s.maxLimit) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.min-limit must be less than or equal to ignotus.foundation.pagination.max-limit .")
            }

            if (s.minLimit > s.defaultLimit) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.min-limit must be less than or equal to ignotus.foundation.pagination.default-limit .")
            }

            if (s.maxLimit < s.defaultLimit) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.max-limit must be greater than or equal to ignotus.foundation.pagination.default-limit .")
            }

            if (!(s.pageBased.enabled || s.offsetBased.enabled)) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.page-based.enabled and ignotus.foundation.pagination.offset-based.enabled need to be activated one at least.")
            }

            field = s
        }
}

open class FoundationPaginationConfig {
    var pageBased: FoundationPaginationPageBasedConfig = FoundationPaginationPageBasedConfig()

    var offsetBased: FoundationPaginationOffsetBasedConfig = FoundationPaginationOffsetBasedConfig()

    var defaultLimit: Int = 20

    var minLimit: Int = 1
        set(s) {
            if (s <= 0) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.min-limit must be greater than 0.")
            }
            field = s
        }

    var maxLimit: Int = 200
        set(s) {
            if (s <= 0) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.max-limit must be greater than 0.")
            }
            field = s
        }
}

class FoundationPaginationPageBasedConfig {
    var enabled: Boolean = true

    var startAt: Int = 0
        set(s) {
            if (s !in 0..1) {
                throw IllegalArgumentException("Configuration ignotus.foundation.pagination.page-based.start-at must be 0 or 1.")
            }
            field = s
        }
}


class FoundationPaginationOffsetBasedConfig {
    var enabled: Boolean = true
}
