package com.tairitsu.ignotus.support.service

import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
open class JSONMapperRegister(private val jacksonNamingStrategyConfig: JacksonNamingStrategyConfig) {

    @Autowired(required = false)
    var objectMapperProvider: JSONMapperProvider? = null

    @PostConstruct
    fun init() {
        if (objectMapperProvider == null) {
            objectMapperProvider = DefaultJSONMapperProvider(jacksonNamingStrategyConfig)
        }

        JSONMapperRegister.objectMapperProvider = objectMapperProvider!!
    }

    companion object {
        var objectMapperProvider: JSONMapperProvider = DefaultJSONMapperProvider()
    }
}
