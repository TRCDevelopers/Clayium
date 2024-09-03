package com.github.trc.clayium.common.recipe.ingredient

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.ItemAndMeta
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

class COreRecipeInput(
    val oreId: Int,
    override val amount: Int,
) : CRecipeInput() {

    constructor(oreDict: String, amount: Int = 1) : this(OreDictionary.getOreID(oreDict), amount)
    constructor(orePrefix: OrePrefix, material: CMaterial, amount: Int = 1) : this(UnificationEntry(orePrefix, material).toString(), amount)

    override val stacks by lazy {
        val oreStacks = OreDictionary.getOres(OreDictionary.getOreName(oreId)).map {
            it.copy().apply { count = amount }
        }
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
        return "${OreDictionary.getOreName(oreId)}($stacks)"
    }
}