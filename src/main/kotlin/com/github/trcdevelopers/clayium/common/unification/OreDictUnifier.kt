package com.github.trcdevelopers.clayium.common.unification

import com.github.trcdevelopers.clayium.common.unification.material.EnumMaterial
import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {

    fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    fun registerOre(stack: ItemStack, enumOrePrefix: EnumOrePrefix, material: Material) {
        //todo
    }

    fun get(oreDict: String, stackSize: Int = 1): ItemStack {
        val ores = OreDictionary.getOres(oreDict)
        if (ores.isEmpty()) return ItemStack.EMPTY
        return ores.first().copy().apply {
            count = stackSize
        }
    }

    fun get(enumOrePrefix: EnumOrePrefix, material: EnumMaterial, stackSize: Int = 1): ItemStack {
        return get(UnificationEntry(enumOrePrefix, material).toString(), stackSize)
    }
}