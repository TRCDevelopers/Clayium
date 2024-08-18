package com.github.trc.clayium.common.recipe.registry

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.OreDictUnifier
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.recipe.Recipe
import com.github.trc.clayium.common.recipe.builder.SimpleRecipeBuilder
import com.github.trc.clayium.common.recipe.ingredient.COreRecipeInput
import net.minecraft.item.ItemStack
import kotlin.math.pow

class CaReactorRecipeRegistry(name: String) : RecipeRegistry<SimpleRecipeBuilder>(name, SimpleRecipeBuilder(), 1, 1) {
    private val actualRecipes by lazy {
        val input = listOf(COreRecipeInput(OrePrefix.gem, CMaterials.antimatter))
        val outputs = listOf(
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter1)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter2)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter3)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter4)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter5)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter6)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.pureAntimatter7)),
           listOf(OreDictUnifier.get(OrePrefix.gem, CMaterials.octuplePureAntimatter)),
        )
        (0..8).map { i ->
            Recipe(input, outputs[i], null, (BASE_CRAFT_TIME * 9.0.pow(i)).toLong(), ClayEnergy.of(100), 10)
        }
    }

    fun findRecipeWithRank(rank: Int, inputs: List<ItemStack>) : Recipe? {
        val index = (rank - 1).coerceIn(0, actualRecipes.size - 1)
        return actualRecipes[index].takeIf { it.matches(inputs) }
    }

    companion object {
        const val BASE_CRAFT_TIME = 300L
    }
}