package com.github.trc.clayium.common.loaders

import com.github.trc.clayium.common.unification.OreDictUnifier
import com.github.trc.clayium.common.unification.material.CMaterials
import com.github.trc.clayium.common.unification.ore.OrePrefix
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