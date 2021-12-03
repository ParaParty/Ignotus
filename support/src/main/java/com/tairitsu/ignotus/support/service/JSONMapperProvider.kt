package com.tairitsu.ignotus.support.service

import com.fasterxml.jackson.databind.ObjectMapper

interface JSONMapperProvider {
    val objectMapper : ObjectMapper
}
