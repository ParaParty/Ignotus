package com.tairitsu.ignotus.support.util

fun String.toGetterFunction(): String =
    if (this[0] in 'a'..'z') {
        "get" + (this[0] - 0x20) + this.substring(1)
    } else {
        "get$this"
    }

fun String.toSmallCamel(): String =
    if (this[0] in 'A'..'Z') {
        (this[0] + 0x20) + this.substring(1)
    } else {
        this
    }
