package com.github.trc.clayium.common.recipe.handler

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterial
import com.github.trc.clayium.api.unification.material.CPropertyKey
import com.github.trc.clayium.api.unification.material.MaterialAmount
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.common.loaders.recipe.CondenserRecipeLoader
import com.github.trc.clayium.common.loaders.recipe.GrinderRecipeLoader
import com.github.trc.clayium.common.recipe.RecipeUtils
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraftforge.fml.common.registry.GameRegistry
import kotlin.math.pow

object MaterialRecipeHandler {
    fun registerRecipes() {
        for (material in ClayiumApi.materialRegistry) {
            GrinderRecipeLoader.handleOre(material)

            if (material.hasProperty(CPropertyKey.CLAY) or material.hasProperty(CPropertyKey.MATTER))
                CondenserRecipeLoader.handleOre(material)

            if (material.hasOre(OrePrefix.ingot)) {
                if (material.hasProperty(CPropertyKey.PLATE)) addPlateRecipe(OrePrefix.ingot, material)
            }

            if (material.hasOre(OrePrefix.gem)) {
                if (material.hasProperty(CPropertyKey.PLATE)) addPlateRecipe(OrePrefix.gem, material)
            }

            if (material.hasOre(OrePrefix.dust)) {
                handleDust(OrePrefix.dust, material)
            }

            if (material.hasOre(OrePrefix.impureDust)) {
                handleImpureDust(material)
            }

            if (material.hasOre(OrePrefix.block)) {
                if (material.hasProperty(CPropertyKey.PLATE) && OrePrefix.block.getMaterialAmount(material) == MaterialAmount.of(1)) {
                    addPlateRecipe(OrePrefix.block, material)
                }
                if (material.hasProperty(CPropertyKey.CLAY)) {
                    val prop = material.getProperty(CPropertyKey.CLAY)
                    if (prop.compressedInto != null) addClayBlockRecipe(material, prop.compressedInto)
                }
            }
        }

        for (markerMaterial in ClayiumApi.markerMaterials) {
            GrinderRecipeLoader.handleOre(markerMaterial)
            CondenserRecipeLoader.handleOre(markerMaterial)
        }
    }

    private fun CMaterial.hasOre(orePrefix: OrePrefix): Boolean {
        return !OreDictUnifier.get(orePrefix, this).isEmpty
    }

    private fun handleDust(dustPrefix: OrePrefix, material: CMaterial) {
        if (material.hasProperty(CPropertyKey.INGOT)) {
            if (material.hasProperty(CPropertyKey.BLAST_SMELTING)) {
                val prop = material.getProperty(CPropertyKey.BLAST_SMELTING)
                CRecipes.CLAY_BLAST_FURNACE.register {
                    input(dustPrefix, material)
                    output(OrePrefix.ingot, material)
                    CEt(prop.factor, prop.tier)
                    duration(prop.duration)
                    tier(prop.tier)
                }
            } else if (material.hasProperty(CPropertyKey.CLAY_SMELTING)) {
                val prop = material.getProperty(CPropertyKey.CLAY_SMELTING)
                CRecipes.SMELTER.register {
                    input(dustPrefix, material)
                    output(OrePrefix.ingot, material)
                    CEt(prop.factor, prop.tier)
                    duration(prop.duration)
                    tier(prop.tier)
                }
            } else {
                GameRegistry.addSmelting(
                    OreDictUnifier.get(OrePrefix.dust, material),
                    OreDictUnifier.get(OrePrefix.ingot, material),
                    0.1f
                )
            }
        }
    }

    private fun handleImpureDust(material: CMaterial) {
        val tier = material.tier?.numeric ?: 0
        val (cePerTick, duration) = when (tier) {
            6 -> ClayEnergy.milli(100) to 100
            7 -> ClayEnergy.of(10) to 300
            8 -> ClayEnergy.of(100) to 1000
            9 -> ClayEnergy.of(1000) to 3000
            else -> ClayEnergy.micro(10) to 1
        }
        CRecipes.ELECTROLYSIS_REACTOR.register {
            input(OrePrefix.impureDust, material)
            output(OrePrefix.dust, material)
            CEt(cePerTick)
            duration(duration)
            tier(tier)
        }
    }

    /**
     * Adds plate and largePlate recipe for [material].
     * Assumes that [material] has a plate property.
     */
    private fun addPlateRecipe(inputPrefix: OrePrefix, material: CMaterial) {
        val plateProperty = material.getProperty(CPropertyKey.PLATE)
        CRecipes.BENDING.register {
            input(inputPrefix, material)
            output(OrePrefix.plate, material)
            CEt(plateProperty.cePerTick)
            duration(plateProperty.requiredTick)
            tier(plateProperty.tier)
        }

        CRecipes.BENDING.register {
            input(OrePrefix.plate, material, 4)
            output(OrePrefix.largePlate, material)
            CEt(plateProperty.cePerTick * 2)
            duration(plateProperty.requiredTick * 2)
            tier(plateProperty.tier)
        }
    }

    private fun addClayBlockRecipe(material: CMaterial, compressedInto: CMaterial) {
        val clayProperty = material.getPropOrNull(CPropertyKey.CLAY)
        val resultClayProperty = compressedInto.getProperty(CPropertyKey.CLAY)
        if (clayProperty != null) {
            // generate recipes for non-energy clay blocks
            if (resultClayProperty.energy == null) {
                RecipeUtils.addShapedRecipe("${compressedInto.materialId.path}_block_compose",
                    OreDictUnifier.get(OrePrefix.block, compressedInto),
                    "CCC",
                    "CCC",
                    "CCC",
                    'C', UnificationEntry(OrePrefix.block, material))

                RecipeUtils.addShapelessRecipe("${compressedInto.materialId.path}_block_decompose",
                    OreDictUnifier.get(OrePrefix.block, material, 9), UnificationEntry(OrePrefix.block, compressedInto))

                CRecipes.DECOMPOSER.register {
                    input(OrePrefix.block, compressedInto)
                    output(OrePrefix.block, material, 9)
                    CEt(ClayEnergy.micro(10))
                    duration(10)
                    tier(0)
                }

                CRecipes.CONDENSER.register {
                    input(OrePrefix.block, material, 9)
                    output(OrePrefix.block, compressedInto)
                    CEt(ClayEnergy.micro(10))
                    duration(10)
                    tier(0)
                }
            } else {
                // generate recipes for energy clay blocks
                CRecipes.ENERGETIC_CLAY_CONDENSER.register {
                    input(OrePrefix.block, material, 9)
                    output(OrePrefix.block, compressedInto)
                    val pow = compressedInto.tier?.numeric?.minus(4)?.coerceAtLeast(0) ?: 0
                    CEt(ClayEnergy.milli((10.0).pow(pow).toLong()))
                    duration(4)
                    tier(3)
                }
            }
        }
    }
}