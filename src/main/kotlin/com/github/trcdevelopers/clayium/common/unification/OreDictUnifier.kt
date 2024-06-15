package com.github.trcdevelopers.clayium.common.unification

import com.github.trcdevelopers.clayium.common.unification.material.Material
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {
    fun get(oreDict: String, stackSize: Int = 1): ItemStack {
        return OreDictionary.getOres(oreDict)[0].copy().apply {
            count = stackSize
        }
    }

    fun get(orePrefix: OrePrefix, material: Material, stackSize: Int = 1): ItemStack {
        return get(UnificationEntry(orePrefix, material).toString(), stackSize)
    }
}