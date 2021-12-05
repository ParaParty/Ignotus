package com.tairitsu.ignotus.support.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class JSONMapperRegister {

    @Autowired(required = false)
    var objectMapperProvider: JSONMapperProvider? = null

    @PostConstruct
    fun init() {
        if (objectMapperProvider == null) {
            objectMapperProvider = DefaultJSONMapperProvider()
        }

        JSONMapperRegister.objectMapperProvider = objectMapperProvider!!
    }

    companion object {
        var objectMapperProvider: JSONMapperProvider = DefaultJSONMapperProvider()
    }
}
