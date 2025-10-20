package io.github.trcdevelopers.clayium.common.recipe.ingredient

import io.github.trcdevelopers.clayium.api.unification.stack.ItemAndMeta
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class CMultiOreRecipeInput(
    override val amount: Int,
    val oreIds: IntArray,
) : CRecipeInput() {

    override val stacks by lazy {
        val oreStacks = oreIds.map {
            OreDictionary.getOres(OreDictionary.getOreName(it)).map { s -> s.copyWithSize(amount) }
        }.flatten()
        oreStacks
    }

    override fun testItemStackAndAmount(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        return stacks.any {
            OreDictionary.itemMatches(it, stack, false) && stack.count >= amount
        }
    }

    override fun testIgnoringAmount(item: ItemAndMeta): Boolean {
        return stacks.any {
            it.item == item.item && it.metadata == item.meta
        }
    }

    override fun toString(): String {
        return "CMultiOreRecipeInput(${oreIds.joinToString(", ") { OreDictionary.getOreName(it) }})"
    }

    companion object {
        fun unifEntries(ores: List<UnificationEntry>, amount: Int): CMultiOreRecipeInput {
            val oreIds = IntArray(ores.size) {
                OreDictionary.getOreID(ores[it].oreName)
            }
            return CMultiOreRecipeInput(amount, oreIds)
        }

        fun oreNames(ores: List<String>, amount: Int): CMultiOreRecipeInput {
            val oreIds = IntArray(ores.size) {
                OreDictionary.getOreID(ores[it])
            }
            return CMultiOreRecipeInput(amount, oreIds)
        }
    }
}