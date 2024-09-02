package com.github.trc.clayium.common.loaders

import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.ClayiumItems
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.ForgeRegistries

object OreDictionaryLoader {
    fun loadOreDictionaries() {
        ClayiumItems.registerOreDicts()
        ClayiumBlocks.registerOreDictionaries()

        with(OreDictUnifier) {
            registerOre(ItemStack(Blocks.CLAY), OrePrefix.block, CMaterials.clay)

            registerOre(ItemStack(Items.COAL), OrePrefix.gem, CMaterials.coal)
            registerOre(ItemStack(Items.COAL, 1, 1), OrePrefix.gem, CMaterials.charcoal)

            val ingot = ForgeRegistries.ITEMS.getValue(clayiumId("meta_ingot"))
            if (ingot != null) {
                val siliconIngot = ItemStack(ingot, 1, CMaterials.silicon.metaItemSubId)
                registerOre(siliconIngot, OrePrefix.item, CMaterials.silicon)
            }
        }
    }
}