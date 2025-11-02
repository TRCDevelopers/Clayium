package io.github.trcdevelopers.clayium.common.loaders.recipe.tofucraftreload

import cn.mcmod.tofucraft.item.ItemLoader
import cn.mcmod.tofucraft.material.TofuType
import io.github.trcdevelopers.clayium.api.unification.material.CMaterials
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.util.copyWithSize
import io.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.item.ItemStack

object TofuCraftRecipeLoader {
    fun registerRecipes() {
        val reactor = CRecipes.CLAY_REACTOR

        reactor.builder()
            .input(ItemLoader.soybeans)
            .input(ItemLoader.nigari)
            .output(TofuType.kinu.itemStack.copyWithSize(4))
            .tier(9).duration(10_000_000_000_000)
            .buildAndRegister()

        reactor.builder()
            .input(TofuType.kinu.itemStack)
            .input("plankWood")
            .output(TofuType.momen.itemStack)
            .tier(7).duration(10_000)
            .buildAndRegister()

        reactor.builder()
            .input(TofuType.momen.itemStack)
            .input("cobblestone")
            .output(TofuType.ishi.itemStack)
            .tier(8).duration(1_000_000_000)
            .buildAndRegister()

        reactor.builder()
            .input(TofuType.ishi.itemStack)
            .input(OrePrefix.ingot, CMaterials.iron)
            .output(TofuType.metal.itemStack)
            .tier(9).duration(10_000_000_000_000)
            .buildAndRegister()

        reactor.builder()
            .input(TofuType.metal.itemStack)
            .input("gemDiamond")
            .output(TofuType.diamond.itemStack)
            .tier(11).duration(10_000_000_000_000_000)
            .buildAndRegister()

        val tofuGem = ItemStack(ItemLoader.material, 1, 18)
        reactor.builder()
            .input(TofuType.momen.itemStack)
            .input("gemDiamond")
            .output(tofuGem)
            .tier(9).duration(10_000_000_000_000)
            .buildAndRegister()
    }
}