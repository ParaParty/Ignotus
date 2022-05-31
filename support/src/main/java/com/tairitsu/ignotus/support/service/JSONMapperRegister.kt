package com.tairitsu.ignotus.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component("IgnotusJSONMapperWrapperRegister")
open class JSONMapperRegister(private val jacksonNamingStrategyConfig: JacksonNamingStrategyConfig) {

    @Autowired(required = false)
    var objectMapper: ObjectMapper? = null

    @Autowired(required = false)
    var objectMapperWrapper: JSONMapperProvider? = null

    @PostConstruct
    fun init() {
        if (objectMapper != null && objectMapperWrapper == null) {
            val mapper = objectMapper!!
            objectMapperWrapper = object : JSONMapperProvider {
                override val objectMapper: ObjectMapper = mapper
            }
        }

        if (objectMapperWrapper == null) {
            objectMapperWrapper = DefaultJSONMapperProvider(jacksonNamingStrategyConfig)
        }

        JSONMapperRegister.objectMapperProvider = objectMapperWrapper!!
    }

    companion object {
        var objectMapperProvider: JSONMapperProvider = DefaultJSONMapperProvider()
    }
}
