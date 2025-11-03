package io.github.trcdevelopers.clayium.common.loaders.recipe.integration.sakura

import cn.mcmod.sakura.block.BlockLoader
import cn.mcmod.sakura.item.ItemLoader
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object SakuraRecipeLoader {
    fun registerRecipes() {

        CRecipes.GRINDER.builder()
            .input(BlockLoader.SAKURA_DIAMOND_ORE)
            .output(ItemLoader.SAKURA_DIAMOND, 2)
            .tier(5).duration(80)
            .buildAndRegister()

        CRecipes.CLAY_REACTOR.builder()
            .input("gemDiamond")
            .input(Items.APPLE)
            .output(ItemLoader.SAKURA_DIAMOND)
            .tier(10).duration(100_000_000_000_000)
    }
}