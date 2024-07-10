package com.github.trcdevelopers.clayium.common.loaders

import com.github.trcdevelopers.clayium.common.unification.OreDictUnifier
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object OreDictionaryLoader {
    fun loadOreDictionaries() {
        with(OreDictUnifier) {
            registerOre(ItemStack(Blocks.CLAY), OrePrefix.block, CMaterials.clay)

            registerOre(ItemStack(Items.COAL), OrePrefix.gem, CMaterials.coal)
            registerOre(ItemStack(Items.COAL, 1, 1), OrePrefix.gem, CMaterials.charcoal)
        }
    }
}