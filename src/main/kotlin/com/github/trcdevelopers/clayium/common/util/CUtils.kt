package com.github.trcdevelopers.clayium.common.util

import com.google.common.base.CaseFormat

object CUtils {
    fun toUpperCamel(snakeCase: String): String {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, snakeCase)
    }

    fun toLowerSnake(camel: String): String {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camel)
    }
}