package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import kotlin.math.min
import kotlin.math.pow

object CondenserRecipeLoader {

    private var siliconeRecipeGenerated = false

    fun registerRecipes() {
        val registry = CRecipes.CONDENSER

        registry
            .builder()
            .input(MetaItemClayParts.CompressedClayShard, 4)
            .output(OrePrefix.block, CMaterials.compressedClay)
            .duration(3)
            .buildAndRegister()
        registry
            .builder()
            .input(MetaItemClayParts.IndustrialClayShard, 4)
            .output(OrePrefix.block, CMaterials.industrialClay)
            .duration(6)
            .buildAndRegister()
        registry
            .builder()
            .input(MetaItemClayParts.AdvancedIndustrialClayShard, 4)
            .output(OrePrefix.block, CMaterials.advancedIndustrialClay)
            .duration(9)
            .buildAndRegister()

        for (i in 0..<(CMaterials.PURE_ANTIMATTERS.size - 1)) {
            registry
                .builder()
                .input(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i], 9)
                .output(OrePrefix.gem, CMaterials.PURE_ANTIMATTERS[i + 1])
                .tier(10)
                .CEt(ClayEnergy.of(100))
                .duration(6)
                .buildAndRegister()
        }
    }

    fun handleOre(material: IMaterial) {
        if (OreDictUnifier.exists(OrePrefix.dust, material)) {
            addDustCondenseRecipe(material)
        }
    }

    private fun addDustCondenseRecipe(material: IMaterial) {
        val tier = min(material.tier?.numeric ?: Int.MAX_VALUE, 4)
        val clayEnergy = ClayEnergy.micro(20 * 10.0.pow(min(tier / 2, 2)).toLong())
        val amount = OrePrefix.block.getMaterialAmount(material).dustAmount
        if (OreDictUnifier.exists(OrePrefix.block, material)) {
            if (material == CMaterials.silicone) {
                // silicone block has 16 variants.
                // only generate the recipe once.
                if (siliconeRecipeGenerated) return
                siliconeRecipeGenerated = true
            }
            CRecipes.CONDENSER.builder()
                .input(OrePrefix.dust, material, amount)
                .output(OrePrefix.block, material)
                .tier(tier)
                .CEt(clayEnergy)
                .duration(5 * amount)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.gem, material)) {
            CRecipes.CONDENSER.builder()
                .input(OrePrefix.dust, material)
                .output(OrePrefix.gem, material)
                .tier(tier)
                .CEt(clayEnergy)
                .duration(5)
                .buildAndRegister()
        }

        if (OreDictUnifier.exists(OrePrefix.crystal, material)) {
            CRecipes.CONDENSER.builder()
                .input(OrePrefix.dust, material)
                .output(OrePrefix.crystal, material)
                .tier(tier)
                .CEt(clayEnergy)
                .duration(5)
                .buildAndRegister()
        }
    }
}
