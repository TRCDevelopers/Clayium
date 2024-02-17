package com.github.trcdevelopers.clayium.common.util

import java.util.Locale

object CUtils {
    fun toUpperCamel(snakeCase: String): String {
        return snakeCase.split("_").joinToString { s -> s.replaceFirstChar { c -> c.titlecase(Locale.ROOT) } }
    }
}