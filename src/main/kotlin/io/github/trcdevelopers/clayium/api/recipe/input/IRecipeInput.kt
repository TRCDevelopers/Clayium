package io.github.trcdevelopers.clayium.api.recipe.input

interface IRecipeInput<T> {
    val components: List<T>
    val requiredAmount: Int
    val consumeAmount: Int
    val isConsumable get() = consumeAmount > 0

    fun test(input: T): Boolean
    fun testIgnoringAmount(input: T): Boolean

    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}