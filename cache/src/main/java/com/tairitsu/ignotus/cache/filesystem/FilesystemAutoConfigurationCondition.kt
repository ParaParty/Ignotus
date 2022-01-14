package com.tairitsu.ignotus.cache.filesystem

import com.tairitsu.ignotus.cache.AutoConfigurationCondition

internal class FilesystemAutoConfigurationCondition : AutoConfigurationCondition() {
    override val type = listOf("filesystem")
}
