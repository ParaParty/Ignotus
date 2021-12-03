package com.tairitsu.ignotus.translation.provider.resource.dsl

import com.tairitsu.ignotus.translation.provider.resource.model.FunctionResourceMessageLine
import com.tairitsu.ignotus.translation.provider.resource.model.ResourceMessageLine
import com.tairitsu.ignotus.translation.provider.resource.model.ResourceMessageNode
import com.tairitsu.ignotus.translation.provider.resource.model.StringResourceMessageLine
import java.util.*

/**
 * 文本字段构造器
 */
interface LanguageLineBuilder {
    /**
     * 文本字段值
     */
    var value: ResourceMessageLine?

    /**
     * 子结点列表
     */
    val children: HashMap<String, LanguageLineBuilder>

    /**
     * 添加一个字符串形式的文本字段
     */
    infix fun String.by(value: String)

    /**
     * 添加一个函数形式的文本字段
     */
    infix fun String.by(value: (FunctionMessageLineArgs) -> String)

    /**
     * 添加子结点
     */
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
    override var value: ResourceMessageLine? = null
    override val children: HashMap<String, LanguageLineBuilder> = HashMap()

    override infix fun String.by(value: LanguageLineBuilder) {
        children[this] = value
    }

    override fun String.by(value: (FunctionMessageLineArgs) -> String) {
        var child = children[this]
        if (child == null) {
            child = LanguageSetBuilder()
            children[this] = child
        }
        child.value = FunctionResourceMessageLine(value)

    }

    override infix fun String.by(value: String) {
        var child = children[this]
        if (child == null) {
            child = LanguageSetBuilder()
            children[this] = child
        }
        child.value = StringResourceMessageLine(value)
    }
}

fun languageSet(block: LanguageLineBuilder.() -> Unit): LanguageSetBuilder {
    val ret = LanguageSetBuilder()
    block(ret)
    return ret
}
