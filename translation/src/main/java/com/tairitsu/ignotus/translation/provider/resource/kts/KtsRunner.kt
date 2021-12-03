package com.tairitsu.ignotus.translation.provider.resource.kts

import java.io.InputStream
import java.io.Reader
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

/**
 * 参考 https://github.com/s1monw1/KtsRunner
 */
class KtsRunner(classLoader: ClassLoader? = Thread.currentThread().contextClassLoader) {
    val engine: ScriptEngine = ScriptEngineManager(classLoader).getEngineByExtension("kts")

    inline fun <reified T> Any?.castOrError(): T = takeIf { it is T }?.let { it as T }
        ?: throw IllegalArgumentException("Cannot cast $this to expected type ${T::class}")

    inline fun <reified T> run(script: String): T =
        kotlin.runCatching { engine.eval(script) }
            .getOrElse { throw LoadException("Cannot load script", it) }
            .castOrError()

    inline fun <reified T> run(reader: Reader): T =
        kotlin.runCatching { engine.eval(reader) }
            .getOrElse { throw LoadException("Cannot load script", it) }
            .castOrError()

    inline fun <reified T> run(inputStream: InputStream): T =
        kotlin.runCatching { engine.eval(inputStream.bufferedReader()) }
            .getOrElse { throw LoadException("Cannot load script", it) }
            .castOrError()
}
