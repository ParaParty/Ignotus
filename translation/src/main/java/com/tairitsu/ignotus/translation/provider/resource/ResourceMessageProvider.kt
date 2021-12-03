package com.tairitsu.ignotus.translation.provider.resource

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.tairitsu.ignotus.translation.provider.MessageProvider
import com.tairitsu.ignotus.translation.provider.resource.dsl.LanguageLineBuilder
import com.tairitsu.ignotus.translation.provider.resource.kts.KtsRunner
import com.tairitsu.ignotus.translation.provider.resource.model.ResourceMessageNode
import com.tairitsu.ignotus.translation.provider.resource.model.StringResourceMessageLine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.support.ResourcePatternUtils
import org.springframework.stereotype.Component
import java.net.JarURLConnection
import java.net.URI
import java.util.*
import javax.annotation.PostConstruct

@Component
class ResourceMessageProvider(private val resourceLoader: ResourceLoader): MessageProvider {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val data = ResourceMessageNode()

    @PostConstruct
    fun init() {
        val t = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources("classpath*:/ignotus/lang/**")
        t.forEach { loadTemplate(it) }
    }

    private fun loadTemplate(resource: Resource?) {
        resource ?: return
        if (!resource.isReadable) {
            return
        }

        val fileName = resource.filename ?: return
        val type = if (fileName.endsWith(".yaml", ignoreCase = true) || fileName.endsWith(".yml", ignoreCase = true)) {
            "yaml"
        } else if (fileName.endsWith(".json", ignoreCase = true) || fileName.endsWith(".jsonc", ignoreCase = true)) {
            "json"
        } else if (fileName.endsWith(".properties", ignoreCase = true)) {
            "properties"
        } else if (fileName.endsWith(".kts", ignoreCase = true)) {
            "kts"
        } else {
            return
        }

        val info = parsePrefix(resource.uri) ?: return
        val prefix = info.second + fileName.split(".").subList(0, fileName.split(".").size - 1).joinToString(".")

        val locale = parseLocale(info.first) ?: return
//        locale.language
//        locale.country
//        locale.variant

        val localeStr = locale.toString().lowercase()
        val localeNode = if (data[localeStr] == null) {
            val t = ResourceMessageNode()
            data[localeStr] = t
            t
        } else {
            data[localeStr]!!
        }

        val node = localeNode.walkOrCreate(prefix)

        when (type) {
            "yaml" -> readResourceYaml(resource, node)
            "json" -> readResourceJson(resource, node)
            "properties" -> readResourceProperties(resource, node)
            "kts" -> readResourceKts(resource, node)
            else -> return
        }
    }

    private fun readResourceYaml(resource: Resource, node: ResourceMessageNode) {
        val mapper = YAMLMapper()
        val map = try {
            mapper.readValue(resource.inputStream, object: TypeReference<Map<String, Any>>() {})
        } catch (e:Exception) {
            log.error("Failed to read resource: ${resource.uri}", e)
            return
        }
        readResource(map, node)
    }

    private fun readResourceJson(resource: Resource, node: ResourceMessageNode) {
        val mapper = ObjectMapper()
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
        @Suppress("DEPRECATION")
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, true)
        val map = try {
            mapper.readValue(resource.inputStream, object: TypeReference<Map<String, Any>>() {})
        } catch (e:Exception) {
            log.error("Failed to read resource: ${resource.uri}", e)
            return
        }
        readResource(map, node)
    }

    private fun readResourceProperties(resource: Resource, node: ResourceMessageNode) {
        val mapper = JavaPropsMapper()
        val map = try {
            mapper.readValue(resource.inputStream, object: TypeReference<Map<String, Any>>() {})
        } catch (e:Exception) {
            log.error("Failed to read resource: ${resource.uri}", e)
            return
        }
        readResource(map, node)
    }

    private fun readResource(map: Map<String, Any>?, node: ResourceMessageNode) {
        if (map == null) {
            return
        }

        map.forEach { (key, value) ->
            if (value is Map<*, *>) {
                val t = ResourceMessageNode()
                node[key] = t
                @Suppress("UNCHECKED_CAST")
                readResource(value as Map<String, Any>, t)
            } else {
                val t = ResourceMessageNode()
                t.value = StringResourceMessageLine(value.toString())
                node[key] = t
            }
        }
    }

    private fun readResourceKts(resource: Resource, node: ResourceMessageNode) {
        try {
            val runner = KtsRunner()
            val languageLineBuilder = runner.run<LanguageLineBuilder>(resource.inputStream)
            languageLineBuilder.build(node)
        } catch (e:Exception) {
            log.error("Failed to read resource: ${resource.uri}", e)
            return
        }
    }

    private fun parseLocale(s: String): Locale? {
        val locale = s.split("_")
        return when (locale.size) {
            1 -> Locale(locale[0])
            2 -> Locale(locale[0], locale[1])
            3 -> Locale(locale[0], locale[1], locale[2])
            else -> null
        }
    }

    private val splitRegex = Regex("""(/|\\)""")

    private fun parsePrefix(uri: URI): Pair<String, String>? {
        if (uri.scheme.equals("file", ignoreCase = true)) {
            // XXX XX XXXXX XXX XXXXX resources main ignotus lang LANG XXXXX XXXXX XXXXXXX XXX XXXXX XXXX.XX
            // 0 PADDING
            // 1 LANG
            // 2 lang
            // 3 ignotus
            // 4 main
            // 5 resources

            // 0 -任意文件夹-> 1
            // 1 -任意文件夹-> 1

            // 1 -lang-> 2
            // 2 -任意文件夹-> 1

            // 2 -ignotus-> 3
            // 2 -任意文件夹-> 1

            // 3 -ignotus-> 4
            // 3 -任意文件夹-> 1

            // 4 -resources-> [5]
            // 4 -任意文件夹-> 1
            val path = uri.path.split(splitRegex)
            var state = 0;
            var idx = path.size - 2;
            if (idx <= 0) {
                return null
            }
            while (idx >= 0) {
                when (state) {
                    0 -> state = 1
                    1 -> state = if (path[idx] == "lang") 2 else 1
                    2 -> state = if (path[idx] == "ignotus") 3 else 1
                    3 -> state = if (path[idx] == "main") 4 else 1
                    4 -> state = if (path[idx] == "resources") 5 else 1
                }
                if (state == 5) {
                    // idx       idx+1 idx+2   idx+3 idx+4
                    // resources main  ignotus lang  LANG  XXXXX XXXXX XXXXXXX XXX XXXXX XXXX.XX
                    return path[idx+4] to path.subList(idx + 5, path.size - 1).joinToString(".")
                }
                idx --
            }
            return null
        }

        if (uri.scheme.equals("jar", ignoreCase = true)) {
            val entryName = (uri.toURL().openConnection() as (JarURLConnection)).entryName
            val path = entryName.split(splitRegex)
            // 0       1    2    3
            // ignotus lang LANG XXXX XXXX.XX
            if (path.size >= 4 && path[0] == "ignotus" && path[1] == "lang") {
                return path[2] to path.subList(3, path.size - 1).joinToString(".")
            }
            return null
        }

        return null
    }

    override fun getTemplate(localeStr: String, key: String, args: Map<String, Any?>, locale: Locale): Pair<Boolean, String> {
        val localeNode = data[localeStr] ?: return false to ""
        val node = localeNode.walkOrNull(key) ?: return false to ""
        return true to node.value!!.get(key, args, locale)
    }
}
