package com.tairitsu.ignotus.cache.mongo

import com.tairitsu.ignotus.cache.AutoConfigurationCondition

internal class MongoAutoConfigurationCondition : AutoConfigurationCondition() {
    override val type = listOf("mongo")
}
