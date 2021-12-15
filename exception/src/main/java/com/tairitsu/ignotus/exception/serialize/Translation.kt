package com.tairitsu.ignotus.exception.serialize

import com.tairitsu.ignotus.support.util.Translation

val translation = Translation.getTranslationService("exception.serialize")

fun translateDetail(code: String) = translation.getMessage("$code.detail")

fun translateDetail(code: String, subType: String) = translation.getMessage("$code.detail_$subType")

fun translateDetail(code: String, args: Map<String, Any>) = translation.getMessage("$code.detail", args)

fun translateDetail(code: String, subType: String, args: Map<String, Any>) =
    translation.getMessage("$code.detail_$subType", args)

fun translateTitle(code: String) = translation.getMessage("$code.title")
