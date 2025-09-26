package io.github.trcdevelopers.clayium.api.recipe.input

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

@Suppress("EqualsOrHashCode")
class COreRecipeInputV2(
    val oreId: Int,
    requiredAmount: Int,
    isConsumable: Boolean = true,
) : CRecipeInputV2(requiredAmount, isConsumable) {

    constructor(oreDict: String, amount: Int = 1, isConsumable: Boolean = true) : this(OreDictionary.getOreID(oreDict), amount, isConsumable)
    constructor(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1, isConsumable: Boolean = true) : this(UnificationEntry(orePrefix, material).toString(), amount, isConsumable)

    override val components: List<ItemStack> by lazy {
        val oreStacks = OreDictionary.getOres(OreDictionary.getOreName(oreId)).map {
            it.copy().apply { count = requiredAmount }
        }
        oreStacks
    }

    override fun testIgnoringAmount(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        return components.any {
            OreDictionary.itemMatches(it, stack, false)
        }
    }

    override fun computeHash(): Int {
        var hash = oreId
        hash = 31 * hash + requiredAmount
        hash = 31 * hash + if (isConsumable) 1 else 0
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is COreRecipeInputV2) return false

        if (oreId != other.oreId) return false
        if (requiredAmount != other.requiredAmount) return false
        if (isConsumable != other.isConsumable) return false

        return true
    }
}