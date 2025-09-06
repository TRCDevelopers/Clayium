package io.github.trcdevelopers.clayium.common.loaders

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials.silicon
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix.Companion.ingot
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import io.github.trcdevelopers.clayium.common.items.ClayiumItems
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

            registerAdditionalOreDict(ingot, silicon, "itemSilicon")
            registerAdditionalOreDict("impureDustAluminum", "dustImpureAluminum")
            registerAdditionalOreDict("impureDustAluminium", "dustImpureAluminium")

            val ingot = ForgeRegistries.ITEMS.getValue(clayiumId("meta_ingot"))
            if (ingot != null) {
                val siliconIngot = ItemStack(ingot, 1, CMaterials.silicon.metaItemSubId)
                registerOre(siliconIngot, OrePrefix.item, CMaterials.silicon)
            }
        }
    }
}