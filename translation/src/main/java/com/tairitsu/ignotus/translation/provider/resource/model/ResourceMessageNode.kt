package com.tairitsu.ignotus.translation.provider.resource.model

/**
 * 文本字段节点
 */
class ResourceMessageNode {
    private val children = HashMap<String, ResourceMessageNode>()

    var value: ResourceMessageLine? = null

    operator fun get(key: String): ResourceMessageNode? {
        return children[key]
    }

    operator fun set(key: String, value: ResourceMessageNode) {
        children[key] = value
    }

    fun walkOrNull(prefix: String): ResourceMessageNode? {
        return walkOrNull(prefix.split("."))
    }

    private fun walkOrNull(prefix: List<String>): ResourceMessageNode? {
        if (prefix.isEmpty()) {
            return this
        }

        val key = prefix[0]
        val node = children[key] ?: return null

        return node.walkOrNull(prefix.subList(1, prefix.size))
    }

    fun walkOrCreate(prefix: String): ResourceMessageNode {
        return walkOrCreate(prefix.split("."))
    }

    private fun walkOrCreate(prefix: List<String>): ResourceMessageNode {
        if (prefix.isEmpty()) {
            return this
        }

        val key = prefix[0]
        val node = children[key]
        if (node == null) {
            val newNode = ResourceMessageNode()
            children[key] = newNode
            return newNode.walkOrCreate(prefix.subList(1, prefix.size))
        }

        return node.walkOrCreate(prefix.subList(1, prefix.size))
    }
}

