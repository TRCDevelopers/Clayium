package com.github.trc.clayium.common.recipe.handler

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.common.recipe.RecipeUtils
import com.github.trc.clayium.common.recipe.registry.CRecipes
import com.github.trc.clayium.common.unification.OreDictUnifier
import com.github.trc.clayium.common.unification.material.Material
import com.github.trc.clayium.common.unification.material.PropertyKey
import com.github.trc.clayium.common.unification.ore.OrePrefix
import com.github.trc.clayium.common.unification.stack.UnificationEntry
import net.minecraftforge.fml.common.registry.GameRegistry
import kotlin.math.pow

object MaterialRecipeHandler {
    fun registerRecipes() {
        for (material in ClayiumApi.materialRegistry) {
            if (material.hasOre(OrePrefix.ingot)) {
                if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.ingot, material)
                tryAddGrindingRecipe(OrePrefix.ingot, material)
            }

            if (material.hasOre(OrePrefix.gem)) {
                if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.gem, material)
                tryAddGrindingRecipe(OrePrefix.gem, material)
            }

            if (material.hasOre(OrePrefix.dust)) {
                handleDust(OrePrefix.dust, material)
            }

            if (material.hasOre(OrePrefix.impureDust)) {
                handleImpureDust(material)
            }

            if (material.hasOre(OrePrefix.block)) {
                handleBlock(material)
            }

            if (material.hasOre(OrePrefix.plate)) { tryAddGrindingRecipe(OrePrefix.plate, material) }
            if (material.hasOre(OrePrefix.largePlate)) { tryAddGrindingRecipe(OrePrefix.largePlate, material, 4) }
        }
    }

    private fun Material.hasOre(orePrefix: OrePrefix): Boolean {
        return !OreDictUnifier.get(orePrefix, this).isEmpty
    }

    private fun handleDust(dustPrefix: OrePrefix, material: Material) {
        val tier = material.tier?.numeric ?: 0
        fun addDustCondenseRecipe(outputPrefix: OrePrefix) {
            if (OreDictUnifier.get(outputPrefix, material).isEmpty) return
            CRecipes.CONDENSER.register {
                input(dustPrefix, material)
                output(outputPrefix, material)
                CEt(if (tier < 10) ClayEnergy.micro(10) else ClayEnergy.micro(250))
                duration(if (tier < 10) 5 else 80)
                tier(if (tier < 10) 0 else 10)
            }
        }

        addDustCondenseRecipe(OrePrefix.block)
        addDustCondenseRecipe(OrePrefix.gem)

        if (material.hasProperty(PropertyKey.INGOT)) {
            if (material.hasProperty(PropertyKey.BLAST_SMELTING)) {
                val prop = material.getProperty(PropertyKey.BLAST_SMELTING)
                CRecipes.CLAY_BLAST_FURNACE.register {
                    input(dustPrefix, material)
                    output(OrePrefix.ingot, material)
                    CEt(prop.factor, prop.tier)
                    duration(prop.duration)
                    tier(prop.tier)
                }
            } else if (material.hasProperty(PropertyKey.CLAY_SMELTING)) {
                val prop = material.getProperty(PropertyKey.CLAY_SMELTING)
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

    private fun handleImpureDust(material: Material) {
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

    private fun handleBlock(material: Material) {
        if (material.hasProperty(PropertyKey.PLATE)) addPlateRecipe(OrePrefix.block, material)
        if (material.hasProperty(PropertyKey.CLAY)) {
            val prop = material.getProperty(PropertyKey.CLAY)
            if (prop.compressedInto != null) addClayBlockRecipe(material, prop.compressedInto)
        }
        tryAddGrindingRecipe(OrePrefix.block, material)
    }

    /**
     * Adds plate and largePlate recipe for [material].
     * Assumes that [material] has a plate property.
     */
    private fun addPlateRecipe(inputPrefix: OrePrefix, material: Material) {
        val plateProperty = material.getProperty(PropertyKey.PLATE)
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

    private fun tryAddGrindingRecipe(inputPrefix: OrePrefix, material: Material, outputAmount: Int = 1) {
        if (OreDictUnifier.get(OrePrefix.dust, material).isEmpty) return
        CRecipes.GRINDER.register {
            input(inputPrefix, material)
            output(OrePrefix.dust, material, outputAmount)
            CEt(ClayEnergy.micro(250))
            duration(4)
            tier(0)
        }
    }

    private fun addClayBlockRecipe(material: Material, compressedInto: Material) {
        val clayProperty = material.getPropOrNull(PropertyKey.CLAY)
        val resultClayProperty = compressedInto.getProperty(PropertyKey.CLAY)
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
                CRecipes.CONDENSER.register {
                    input(OrePrefix.block, material, 9)
                    output(OrePrefix.block, compressedInto)
                    val pow = compressedInto.tier?.numeric?.minus(4)?.coerceAtLeast(0) ?: 0
                    CEt(ClayEnergy.milli((10.0).pow(pow).toLong()))
                    duration(4)
                    tier(4)
                }
            }
        }
    }
}