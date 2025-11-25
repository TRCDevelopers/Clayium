package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.projectred

import io.github.trcdevelopers.clayium.api.unification.OreDictUnifier
import io.github.trcdevelopers.clayium.api.unification.material.CMarkerMaterials
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.Mods
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object ProjectRedRecipeLoader {
    fun registerRecipes() {
        if (!(Mods.GregTech.isModLoaded || OreDictUnifier.exists(OrePrefix.ingot, CMarkerMaterials.conductiveIron))) {
            CRecipes.ALLOY_SMELTER.builder()
                .input(CMaterials.iron)
                .input(Items.REDSTONE, 8)
                .output(OrePrefix.ingot, CMarkerMaterials.redAlloy)
                .tier(6).duration(100)
                .buildAndRegister()
        } else {
            CRecipes.ALLOY_SMELTER.builder()
                .input(CMaterials.copper)
                .input(Items.REDSTONE, 4)
                .output(OrePrefix.ingot, CMarkerMaterials.redAlloy)
                .tier(6).duration(100)
                .buildAndRegister()
        }

        CRecipes.ALLOY_SMELTER.builder()
            .input(CMaterials.iron)
            .input(CMarkerMaterials.electrotine, 8)
            .output(OrePrefix.ingot, CMarkerMaterials.electrotineAlloy)
            .tier(6).duration(100)
            .buildAndRegister()
    }
}