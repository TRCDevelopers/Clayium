package com.github.trcdevelopers.clayium.api.capability

fun interface IConfigurationTool {

    fun getType(isSneaking: Boolean): ToolType?

    enum class ToolType {
        PIPING,
        INSERTION,
        EXTRACTION,
        ROTATION,
        FILTER_REMOVER,
    }
}