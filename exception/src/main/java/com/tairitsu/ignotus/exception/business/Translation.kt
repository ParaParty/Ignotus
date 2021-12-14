package com.tairitsu.ignotus.exception.business

import com.tairitsu.ignotus.support.util.Translation

val translation = Translation.getTranslationService("exception.business")

fun translateDetail(code: String) = translation.lang("$code.detail")

fun translateDetail(code: String, subType: String) = translation.lang("$code.detail_$subType")

fun translateDetail(code: String, args: Map<String, Any>) = translation.lang("$code.detail", args)

fun translateDetail(code: String, subType: String, args: Map<String, Any>) =
    translation.lang("$code.detail_$subType", args)

fun translateTitle(code: String) = translation.lang("$code.title")
