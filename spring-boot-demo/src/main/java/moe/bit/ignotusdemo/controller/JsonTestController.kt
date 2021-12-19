package moe.bit.ignotusdemo.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.tairitsu.ignotus.foundation.model.JsonApiObjectBody
import com.tairitsu.ignotus.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class JsonTestController {

    @Autowired
    private lateinit var validator: Validator

    @PostMapping("/testJson")
    open fun getJson(@RequestBody body: Map<String, Any?>): Any {
        val typeRef = object : TypeReference<JsonApiObjectBody<TestObject>>() {}

        val data = validator.validate(body, mapOf(
            "data.attributes.testField" to "required",
        ), typeRef)

        return data
    }
}

data class TestObject(var testField: String)
