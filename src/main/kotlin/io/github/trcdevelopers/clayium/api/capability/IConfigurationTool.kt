package io.github.trcdevelopers.clayium.api.capability

/**
 * Capability interface for Items.
 */
fun interface IConfigurationTool {

    fun getType(isSneaking: Boolean): ToolType?

    // TODO: enum is not extensible. Forge has an API to extend enums. but it doesn't work with Kotlin when expressions.
    enum class ToolType {
        PIPING,
        INSERTION,
        EXTRACTION,
        ROTATION,
        FILTER_REMOVER,
    }
}