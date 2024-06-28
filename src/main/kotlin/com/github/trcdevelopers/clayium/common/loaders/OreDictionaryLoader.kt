package com.github.trcdevelopers.clayium.common.loaders

import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

object OreDictionaryLoader {
    fun registerOreDicts() {
        OreDictUnifier.registerOre(ItemStack(Blocks.CLAY), OrePrefix.block, CMaterials.clay)
    }
}