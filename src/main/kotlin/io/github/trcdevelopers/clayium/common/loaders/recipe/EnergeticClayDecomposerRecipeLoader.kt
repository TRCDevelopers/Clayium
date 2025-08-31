package io.github.trcdevelopers.clayium.common.loaders.recipe

import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object EnergeticClayDecomposerRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.ENERGETIC_CLAY_DECOMPOSER

        registry.builder()
            .input(OrePrefix.block, CMaterials.clay)
            .output(Items.CLAY_BALL, 4)
            .tier(13).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()
        for (ls in CMaterials.CLAYS.windowed(2)) {
            val (lowTierClay, highTierClay) = ls
            if (lowTierClay == CMaterials.clay) continue
            registry.builder()
                .input(OrePrefix.block, highTierClay)
                .output(OrePrefix.block, lowTierClay, 9)
                .tier(13).CEt(ClayEnergy.micro(10)).duration(1)
                .buildAndRegister()
        }
    }
}