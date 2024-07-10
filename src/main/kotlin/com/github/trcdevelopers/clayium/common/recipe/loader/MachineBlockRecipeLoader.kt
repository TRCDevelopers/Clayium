package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.RecipeUtils
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack

object MachineBlockRecipeLoader {
    fun registerRecipes() {
        //region Hulls
        val mainHullMaterials = listOf(
            CMaterials.clay,
            CMaterials.denseClay,
            CMaterials.industrialClay,
            CMaterials.advancedIndustrialClay,
            CMaterials.impureSilicon,
            CMaterials.aluminum,
            CMaterials.claySteel,
            CMaterials.clayium,
            CMaterials.ultimateAlloy,
            CMaterials.antimatter,
            CMaterials.pureAntimatter,
            CMaterials.octupleEnergyClay,
            CMaterials.octuplePureAntimatter,
        )

        val circuits = listOf(
            MetaItemClayParts.CLAY_GEAR,
            MetaItemClayParts.CLAY_CIRCUIT,
            MetaItemClayParts.SIMPLE_CIRCUIT,
            MetaItemClayParts.BASIC_CIRCUIT,
            MetaItemClayParts.ADVANCED_CIRCUIT,
            MetaItemClayParts.PRECISION_CIRCUIT,
            MetaItemClayParts.INTEGRATED_CIRCUIT,
            MetaItemClayParts.CLAY_CORE,
            MetaItemClayParts.CLAY_BRAIN,
            MetaItemClayParts.CLAY_SPIRIT,
            MetaItemClayParts.CLAY_SOUL,
            MetaItemClayParts.CLAY_ANIMA,
            MetaItemClayParts.CLAY_PSYCHE,
        )

        for (i in 1..2) {
            RecipeUtils.addShapedRecipe("machine_hull_$i", ItemStack(ClayiumBlocks.MACHINE_HULL, 1, i + 1),
                "PPP", "PCP", "PPP",
                'C', circuits[i],
                'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i]))
        }
        for (i in 3..12) {
            if (i == 4) {
                RecipeUtils.addShapedRecipe("machine_hull_$i", ItemStack(ClayiumBlocks.MACHINE_HULL, 1, i + 1),
                    "PEP", "SCS", "PSP",
                    'E', MetaItemClayParts.CEE,
                    'C', circuits[i],
                    'S', UnificationEntry(OrePrefix.largePlate, CMaterials.silicone),
                    'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i]))
            } else {
                RecipeUtils.addShapedRecipe("machine_hull_$i", ItemStack(ClayiumBlocks.MACHINE_HULL, 1, i + 1),
                    "PEP", "PCP", "PPP",
                    'E', MetaItemClayParts.CEE,
                    'C', circuits[i],
                    'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i]))
            }
        }
        //endregion
    }
}