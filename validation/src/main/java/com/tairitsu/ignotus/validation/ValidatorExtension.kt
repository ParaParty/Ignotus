package com.tairitsu.ignotus.validation

object ValidatorExtension {
    inline fun <reified T> Validator.validate(
        content: Map<String, Any?>,
        validation: Map<String, Any>,
        basePath: String = "",
    ): T = this.validate(content, validation, basePath, T::class.java)

    inline fun <reified T> Validator.validate(
        content: String,
        validation: Map<String, Any>,
        basePath: String = "",
    ): T = this.validate(content, validation, basePath, T::class.java)
}
