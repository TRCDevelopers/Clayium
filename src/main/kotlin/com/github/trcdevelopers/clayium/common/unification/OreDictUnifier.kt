package com.github.trcdevelopers.clayium.common.unification

import com.github.trcdevelopers.clayium.common.unification.material.Material
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {
    fun get(orePrefix: OrePrefix, material: Material, stackSize: Int = 1): ItemStack {
        return OreDictionary.getOres(orePrefix.concat(material))[0]
    }
}