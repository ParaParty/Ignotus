package com.tairitsu.ignotus.support.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.springframework.stereotype.Component

@Component
class DefaultJSONMapperProvider : JSONMapperProvider {
    override val objectMapper: ObjectMapper = JsonMapper.builder()
        .addModule(ParameterNamesModule())
        .addModule(Jdk8Module())
        .addModule(JavaTimeModule())
        .addModule(KotlinModule.Builder().build())
        .build()
}
