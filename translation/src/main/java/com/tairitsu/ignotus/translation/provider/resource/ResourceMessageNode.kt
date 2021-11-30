package com.tairitsu.ignotus.translation.provider.resource

class ResourceMessageNode {
    private val children = HashMap<String, ResourceMessageNode>()

    var value = ""

    operator fun get(key: String): ResourceMessageNode? {
        return children[key]
    }

    operator fun set(localeStr: String, value: ResourceMessageNode) {
        children[localeStr] = value
    }

}
