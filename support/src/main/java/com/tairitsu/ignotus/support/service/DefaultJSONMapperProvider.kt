package com.tairitsu.ignotus.support.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.tairitsu.ignotus.support.config.JacksonNamingStrategyConfig

class DefaultJSONMapperProvider() : JSONMapperProvider {

    @Suppress("UNUSED_PARAMETER")
    constructor(jacksonNamingStrategyConfig: JacksonNamingStrategyConfig) : this() {
        objectMapper.propertyNamingStrategy = JacksonNamingStrategyConfig.namingStrategy
    }

    override val objectMapper: ObjectMapper = JsonMapper.builder()
        .findAndAddModules()
        .build()

    init {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
