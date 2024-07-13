package com.github.trc.clayium.common.unification

import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.ore.OrePrefix
import com.github.trc.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {

    fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    fun registerOre(stack: ItemStack, orePrefix: OrePrefix, material: Material) {
        registerOre(stack, UnificationEntry(orePrefix, material).toString())
    }

    fun get(oreDict: String, stackSize: Int = 1): ItemStack {
        val ores = OreDictionary.getOres(oreDict)
        if (ores.isEmpty()) return ItemStack.EMPTY
        return ores.first().copy().apply {
            count = stackSize
        }
    }

    fun get(orePrefix: OrePrefix, material: Material, stackSize: Int = 1): ItemStack {
        return get(UnificationEntry(orePrefix, material).toString(), stackSize)
    }
}