package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.CMaterials.clay
import com.github.trc.clayium.api.unification.material.CMaterials.denseClay
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.ClayiumItems
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.RecipeUtils
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object CraftingRecipeLoader {
    fun registerRecipes() {
        clayToolRecipes()
        registerClayPartsRecipes()

        RecipeUtils.addShapedRecipe("clay_work_table",
            ItemStack(ClayiumBlocks.CLAY_WORK_TABLE),
            "CC", "CC", 'C', UnificationEntry(OrePrefix.block, denseClay))

        RecipeUtils.addSmeltingRecipe(UnificationEntry(OrePrefix.ingot, CMaterials.impureSilicon),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.silicone), 0.1f)

        RecipeUtils.addShapelessRecipe("ultimate_compound_ingot", OreDictUnifier.get(OrePrefix.ingot, CMaterials.ultimateCompound, 9),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.strontium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.barium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.calcium),
            OreDictUnifier.get(OrePrefix.ingot, CMaterials.clayium),
            *Array(5) { OreDictUnifier.get(OrePrefix.ingot, CMaterials.aluminum) })

        for (i in 1..<CMaterials.PURE_ANTIMATTERS.size) {
            RecipeUtils.addShapelessRecipe("pure_antimatter_decompose_$i",
                OreDictUnifier.get(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i - 1], 9),
                OreDictUnifier.get(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i]))
        }

        for (material in ClayiumApi.materialRegistry) {
            if (!OrePrefix.block.isIgnored(material) && OreDictUnifier.exists(OrePrefix.block, material)) {
                val orePrefix = if (OreDictUnifier.exists(OrePrefix.ingot, material))
                    OrePrefix.ingot
                else if (OreDictUnifier.exists(OrePrefix.gem, material))
                    OrePrefix.gem
                else
                    continue
                RecipeUtils.addShapedRecipe("${material.materialId}_compress",
                    OreDictUnifier.get(OrePrefix.block, material), "III", "III", "III", 'I',
                    UnificationEntry(orePrefix, material))

                RecipeUtils.addShapelessRecipe("${material.materialId}_decompress",
                    OreDictUnifier.get(orePrefix, material, 9), UnificationEntry(OrePrefix.block, material))
            }
        }
    }

    private fun clayToolRecipes() {
        RecipeUtils.addShapedRecipe("raw_rolling_pin",
            MetaItemClayParts.RawClayRollingPin.getStackForm(),
            "sCs",
            's', UnificationEntry(OrePrefix.shortStick, clay),
            'C', UnificationEntry(OrePrefix.cylinder, clay))
        RecipeUtils.addShapedRecipe("raw_spatula",
            MetaItemClayParts.RawClaySpatula.getStackForm(),
            "sBs",
            's', UnificationEntry(OrePrefix.shortStick, clay),
            'B', UnificationEntry(OrePrefix.blade, clay))

        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClayRollingPin.getStackForm(), ItemStack(ClayiumItems.CLAY_ROLLING_PIN))
        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClaySlicer.getStackForm(), ItemStack(ClayiumItems.CLAY_SLICER))
        RecipeUtils.addSmeltingRecipe(MetaItemClayParts.RawClaySpatula.getStackForm(), ItemStack(ClayiumItems.CLAY_SPATULA))

        RecipeUtils.addShapedRecipe("clay_wrench", ItemStack(ClayiumItems.CLAY_WRENCH),
            "B B", " C ", " S ",
            'B', UnificationEntry(OrePrefix.blade, denseClay),
            'C', UnificationEntry(OrePrefix.spindle, denseClay),
            'S', UnificationEntry(OrePrefix.stick, denseClay))

        RecipeUtils.addShapedRecipe("clay_shovel", ItemStack(ClayiumItems.CLAY_SHOVEL),
            "H", "I", "I",
            'H', UnificationEntry(OrePrefix.plate, clay),
            'I', UnificationEntry(OrePrefix.stick, clay))
        RecipeUtils.addShapedRecipe("clay_pickaxe", ItemStack(ClayiumItems.CLAY_PICKAXE),
            "HHH", " I ", " I ",
            'H', UnificationEntry(OrePrefix.plate, denseClay),
            'I', UnificationEntry(OrePrefix.stick, denseClay))
        RecipeUtils.addShapedRecipe("clay_steel_pickaxe", ItemStack(ClayiumItems.CLAY_STEEL_PICKAXE),
            "HHH", " I ", " I ",
            'H', UnificationEntry(OrePrefix.ingot, CMaterials.claySteel),
            'I', UnificationEntry(OrePrefix.stick, denseClay))
    }

    private fun registerClayPartsRecipes() {

        RecipeUtils.addShapelessRecipe("large_clay_ball", MetaItemClayParts.LargeClayBall.getStackForm(),
            *Array(8) { Items.CLAY_BALL })
        RecipeUtils.addShapelessRecipe("clay_short_stick",
            OreDictUnifier.get(OrePrefix.shortStick, clay, 2), UnificationEntry(OrePrefix.stick, clay))
        RecipeUtils.addShapelessRecipe("clay_small_ring_loop",
            OreDictUnifier.get(OrePrefix.smallRing, clay), UnificationEntry(OrePrefix.shortStick, clay))
        RecipeUtils.addShapelessRecipe("clay_short_stick_loop",
            OreDictUnifier.get(OrePrefix.shortStick, clay), UnificationEntry(OrePrefix.smallRing, clay))
        RecipeUtils.addShapelessRecipe("clay_ring",
            OreDictUnifier.get(OrePrefix.ring, clay), UnificationEntry(OrePrefix.cylinder, clay))
        RecipeUtils.addShapelessRecipe("clay_pipe",
            OreDictUnifier.get(OrePrefix.pipe, clay), UnificationEntry(OrePrefix.plate, clay))

        RecipeUtils.addShapedRecipe("large_clay_plate",
            OreDictUnifier.get(OrePrefix.largePlate, clay),
            "CCC", "CCC", "CCC",
            'C', UnificationEntry(OrePrefix.plate, clay))
        RecipeUtils.addShapedRecipe("clay_circuit",
            MetaItemClayParts.ClayCircuit.getStackForm(),
            "SGS", "RBR", "SGS",
            'S', UnificationEntry(OrePrefix.stick, denseClay),
            'G', UnificationEntry(OrePrefix.gear, denseClay),
            'R', UnificationEntry(OrePrefix.ring, denseClay),
            'B', MetaItemClayParts.ClayCircuitBoard)
        RecipeUtils.addShapedRecipe("simple_circuit",
            MetaItemClayParts.SimpleCircuit.getStackForm(),
            "DDD", "DBD", "DDD",
            'D', MetaItemClayParts.EnergizedClayDust,
            'B', MetaItemClayParts.ClayCircuitBoard)

        for (m in listOf(clay, denseClay)) {
            RecipeUtils.addShapedRecipe("${m.materialId.path}_gear",
                OreDictUnifier.get(OrePrefix.gear, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.shortStick, m),
                'C', UnificationEntry(OrePrefix.smallRing, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_cutting_head",
                OreDictUnifier.get(OrePrefix.cuttingHead, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.blade, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_bearing",
                OreDictUnifier.get(OrePrefix.bearing, m),
                "III", "ICI", "III",
                'I', Items.CLAY_BALL,
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_grinding_head",
                OreDictUnifier.get(OrePrefix.grindingHead, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.needle, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_water_wheel",
                OreDictUnifier.get(OrePrefix.wheel, m),
                "III", "ICI", "III",
                'I', UnificationEntry(OrePrefix.plate, m),
                'C', UnificationEntry(OrePrefix.ring, m))
            RecipeUtils.addShapedRecipe("${m.materialId.path}_spindle",
                OreDictUnifier.get(OrePrefix.spindle, m),
                "rPr", "SBR", "rPr",
                'r', UnificationEntry(OrePrefix.smallRing, m),
                'R', UnificationEntry(OrePrefix.ring, m),
                'P', UnificationEntry(OrePrefix.plate, m),
                'S', UnificationEntry(OrePrefix.stick, m),
                'B', UnificationEntry(OrePrefix.bearing, m))
        }
    }
}