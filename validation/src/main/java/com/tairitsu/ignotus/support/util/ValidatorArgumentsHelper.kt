package com.tairitsu.ignotus.support.util

object ValidatorArgumentsHelper {
    fun fromString(arg: Any?): HashMap<String, String> = HashMap<String, String>().also {
        if (arg is String && arg.length > 0) {
            val args = arg.split(',')
            val size = args.size;
            for (i in 0 until size) {
                val s = args[i];
                if (s.isEmpty()) continue;

                val pair = s.split(Regex("="), 2);
                if (pair.size != 2) continue;

                it[pair[0]] = pair[1];
            }
        }
    }

    fun tryParse(arg: Any?): HashMap<String, String> =
        when (arg) {
            is String -> fromString(arg);
            else -> HashMap<String, String>()
        }

    fun parseToDouble(arg: Any): Double =
        when (arg) {
            is String -> arg.toDouble()
            is Number -> arg.toDouble()
            else -> 0.0
        }

    fun parseToFloat(arg: Any): Float =
        when (arg) {
            is String -> arg.toFloat()
            is Number -> arg.toFloat()
            else -> 0.0f
        }

    fun parseToInt(arg: Any): Int =
        when (arg) {
            is String -> arg.toInt()
            is Number -> arg.toInt()
            else -> 0
        }

    fun parseToLong(arg: Any): Long =
        when (arg) {
            is String -> arg.toLong()
            is Number -> arg.toLong()
            else -> 0
        }
}
