package io.github.trcdevelopers.clayium.common.loaders.recipe.sakura

import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

private typealias SakuraItems = cn.mcmod.sakura.item.ItemLoader
private typealias SakuraBlocks = cn.mcmod.sakura.block.BlockLoader

object SakuraRecipeLoader {
    fun registerRecipes() {
        CRecipes.GRINDER.builder()
            .input(SakuraBlocks.SAKURA_DIAMOND_ORE)
            .output(SakuraItems.SAKURA_DIAMOND, 2)
            .tier(5).duration(80)
            .buildAndRegister()

        CRecipes.CLAY_REACTOR.builder()
            .input("gemDiamond")
            .input(Items.APPLE)
            .output(SakuraItems.SAKURA_DIAMOND)
            .tier(10).duration(100_000_000_000_000)
    }
}