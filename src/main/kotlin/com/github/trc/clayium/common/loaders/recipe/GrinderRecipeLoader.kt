package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.material.MaterialAmount
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.bearing
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.blade
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.block
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.crystal
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.cuttingHead
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.cylinder
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.disc
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.gear
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.gem
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.grindingHead
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.ingot
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.needle
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.pipe
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.ring
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.spindle
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

object GrinderRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.GRINDER

        registry
            .builder()
            .input(ClayiumBlocks.CLAY_ORE)
            .output(MetaItemClayParts.CompressedClayShard, 2)
            .duration(3)
            .buildAndRegister()
        registry
            .builder()
            .input(ClayiumBlocks.DENSE_CLAY_ORE)
            .output(MetaItemClayParts.IndustrialClayShard, 3)
            .duration(6)
            .buildAndRegister()
        registry
            .builder()
            .input(ClayiumBlocks.LARGE_DENSE_CLAY_ORE)
            .output(MetaItemClayParts.AdvancedIndustrialClayShard, 5)
            .duration(9)
            .buildAndRegister()
        registry
            .builder()
            .input(ClayiumBlocks.CLAY_TREE_LOG)
            .output(OrePrefix.dust, CMaterials.organicClay)
            .tier(5)
            .defaultCEt()
            .duration(200)
            .buildAndRegister()

        registry
            .builder()
            .input(Blocks.COBBLESTONE)
            .output(Blocks.GRAVEL)
            .tier(0)
            .CEt(ClayEnergy.micro(10))
            .duration(10)
            .buildAndRegister()
        registry
            .builder()
            .input(Blocks.COBBLESTONE, 16)
            .output(Blocks.GRAVEL, 16)
            .tier(1)
            .CEt(ClayEnergy.micro(10))
            .duration(10)
            .buildAndRegister()
        registry
            .builder()
            .input(Blocks.COBBLESTONE, 64)
            .output(Blocks.GRAVEL, 64)
            .tier(2)
            .CEt(ClayEnergy.micro(10))
            .duration(10)
            .buildAndRegister()

        // clay block grinding
        for ((i, m) in
            listOf(
                    CMaterials.clay,
                    CMaterials.denseClay,
                    CMaterials.industrialClay,
                    CMaterials.advancedIndustrialClay
                )
                .withIndex()) {
            registry
                .builder()
                .input(block, m)
                .output(OrePrefix.dust, m)
                .tier(0)
                .defaultCEt()
                .duration(4 * (i + 1))
                .buildAndRegister()
        }
    }

    fun handleOre(material: IMaterial) {
        // skip if no dust
        if (!OreDictUnifier.exists(OrePrefix.dust, material)) {
            return
        }
        for (prefix in OrePrefix.metaItemPrefixes) {
            if (!OreDictUnifier.exists(prefix, material)) continue
            when (prefix) {
                block -> handleBlockGrinding(material)
                ingot,
                gem,
                crystal,
                bearing,
                blade,
                cuttingHead,
                cylinder,
                disc,
                gear,
                grindingHead,
                needle,
                pipe,
                ring,
                spindle -> {
                    addDefaultGrindingRecipe(prefix, material)
                }
            }
        }
    }

    private fun handleBlockGrinding(material: IMaterial) {
        // skip if it's a clay block. (energy, duration) of these are special
        if (
            material === CMaterials.clay ||
                material === CMaterials.denseClay ||
                material === CMaterials.industrialClay ||
                material === CMaterials.advancedIndustrialClay
        ) {
            return
        }
        addDefaultGrindingRecipe(block, material)
    }

    private fun addDefaultGrindingRecipe(orePrefix: OrePrefix, material: IMaterial) {
        val tier = min(material.tier?.numeric ?: Int.MAX_VALUE, 5)
        val clayEnergy = ClayEnergy.micro(20 * 10.0.pow(min(tier / 2, 2)).toLong())
        val mAmount = orePrefix.getMaterialAmount(material)
        if (mAmount == MaterialAmount.NONE) return
        val durationModifier = floor(sqrt(mAmount.dustAmount.toDouble())).toInt()
        val amount = mAmount.dustAmount.toInt()
        CRecipes.GRINDER.builder()
            .input(orePrefix, material)
            .output(OrePrefix.dust, material, amount)
            .tier(tier)
            .CEt(clayEnergy)
            .duration(80 * durationModifier)
            .buildAndRegister()
    }
}
