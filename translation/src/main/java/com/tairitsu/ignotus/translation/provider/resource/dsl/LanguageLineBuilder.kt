package com.tairitsu.ignotus.translation.provider.resource.dsl

import com.tairitsu.ignotus.translation.provider.resource.ResourceMessageNode

interface LanguageLineBuilder {
    var value: String?
    val children: HashMap<String, LanguageLineBuilder>

    infix fun String.by(value: String)

    infix fun String.by(value: LanguageLineBuilder)

    fun build(): ResourceMessageNode {
        val node = ResourceMessageNode()
        build(node)
        return node
    }

    fun build(now: ResourceMessageNode): ResourceMessageNode {
        now.value = value
        children.forEach { (key, value) ->
            val next = now.walkOrCreate(key)
            value.build(next)
        }
        return now
    }
}

class LanguageSetBuilder : LanguageLineBuilder {
    override var value: String? = null
    override val children: HashMap<String, LanguageLineBuilder> = HashMap()

    override infix fun String.by(value: LanguageLineBuilder) {
        children[this] = value
    }

    override infix fun String.by(value: String) {
        var child = children[this]
        if (child == null) {
            child = LanguageSetBuilder()
            children[this] = child
        }
        child.value = value
    }
}

fun languageSet(block: LanguageLineBuilder.() -> Unit): LanguageSetBuilder {
    val ret = LanguageSetBuilder()
    block(ret)
    return ret
}
