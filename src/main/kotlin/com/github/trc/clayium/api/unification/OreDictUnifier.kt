package com.github.trc.clayium.api.unification

import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {

    fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    fun registerOre(stack: ItemStack, orePrefix: OrePrefix, material: CMaterial) {
        registerOre(stack, UnificationEntry(orePrefix, material).toString())
    }

    fun get(oreDict: String, stackSize: Int = 1): ItemStack {
        val ores = OreDictionary.getOres(oreDict)
        if (ores.isEmpty()) return ItemStack.EMPTY
        return ores.first().copy().apply {
            count = stackSize
        }
    }

    fun get(orePrefix: OrePrefix, material: CMaterial, stackSize: Int = 1): ItemStack {
        return get(UnificationEntry(orePrefix, material).toString(), stackSize)
    }
}