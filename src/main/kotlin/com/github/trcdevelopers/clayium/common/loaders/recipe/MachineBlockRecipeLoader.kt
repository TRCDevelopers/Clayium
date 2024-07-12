package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ClayTiers.*
import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.metatileentity.MetaTileEntities
import com.github.trcdevelopers.clayium.common.recipe.RecipeUtils
import com.github.trcdevelopers.clayium.common.recipe.builder.RecipeBuilder
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import com.github.trcdevelopers.clayium.common.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack

object MachineBlockRecipeLoader {
    fun registerRecipes() {
        //region Hulls
        val mainHullMaterials = listOf(
            CMaterials.clay,
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

        val circuits: List<Any> = listOf(
            Unit, // not used, but needed for indexing
            UnificationEntry(OrePrefix.gear, CMaterials.clay),
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

        for (tier in ClayTiers.entries) {
            val hull = ClayiumBlocks.MACHINE_HULL
            val i = tier.numeric
            when (tier) {
                DEFAULT, CLAY -> {}
                DENSE_CLAY ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PPP", "PCP", "PPP",
                        'C', circuits[i],
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                SIMPLE, BASIC, PRECISION, CLAY_STEEL, CLAYIUM,
                ULTIMATE, ANTIMATTER, PURE_ANTIMATTER, OEC, OPA ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PEP", "PCP", "PPP",
                        'E', MetaItemClayParts.CEE,
                        'C', circuits[i],
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                ADVANCED ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PEP", "SCS", "PSP",
                        'E', MetaItemClayParts.CEE,
                        'C', circuits[i],
                        'S', UnificationEntry(OrePrefix.largePlate, CMaterials.silicone),
                        'P', UnificationEntry(OrePrefix.largePlate, mainHullMaterials[i])
                    )
                AZ91D -> CRecipes.ASSEMBLER.builder()
                    .input(OrePrefix.largePlate, CMaterials.az91d)
                    .input(MetaItemClayParts.PRECISION_CIRCUIT)
                    .output(hull.getItem(tier))
                    .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
                    .buildAndRegister()
                ZK60A ->
                    RecipeUtils.addShapedRecipe("machine_hull_${tier.lowerName}", hull.getItem(tier),
                        "PPP", "PCP", "PPP",
                        'C', MetaItemClayParts.PRECISION_CIRCUIT,
                        'P', UnificationEntry(OrePrefix.largePlate, CMaterials.zk60a)
                    )
            }
        }
        //endregion

        registerAssembler(MetaTileEntities.BENDING_MACHINE) {
            input(OrePrefix.plate, CMaterials.denseClay, 3)
        }
    }

    private fun registerAssembler(metaTileEntities: List<MetaTileEntity>, inputProvider: RecipeBuilder<*>.() -> RecipeBuilder<*>) {
        for (mte in metaTileEntities) {
            CRecipes.ASSEMBLER.builder()
                .input(ItemStack(ClayiumBlocks.MACHINE_HULL, 1, mte.tier.numeric))
                .output(mte.getStackForm())
                .tier(4)
                .CEt(1.0).duration(60)
                .inputProvider()
                .buildAndRegister()
        }
    }
}