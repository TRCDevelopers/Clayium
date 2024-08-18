package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Blocks

object CentrifugeRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.CENTRIFUGE

        registry.builder()
            .input(OrePrefix.dust, CMaterials.clay, 9)
            .output(OrePrefix.dust, CMaterials.denseClay)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(OrePrefix.dust, CMaterials.denseClay, 2)
            .output(OrePrefix.dust, CMaterials.clay, 9)
            .output(OrePrefix.dust, CMaterials.calcareousClay)
            .tier(0).CEt(ClayEnergy.micro(20)).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay, 2)
            .output(MetaItemClayParts.EnergizedClayDust, 12)
            .output(OrePrefix.dust, CMaterials.clay, 8)
            .output(OrePrefix.dust, CMaterials.denseClay, 8)
            .output(OrePrefix.dust, CMaterials.industrialClay)
            .tier(0).CEt(ClayEnergy.micro(40)).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(OrePrefix.dust, CMaterials.advancedIndustrialClay, 2)
            .output(MetaItemClayParts.EnergizedClayDust, 64)
            .output(OrePrefix.dust, CMaterials.clay, 64)
            .output(OrePrefix.dust, CMaterials.denseClay, 64)
            .output(OrePrefix.dust, CMaterials.industrialClay, 12)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(12)
            .buildAndRegister()

        registry.builder()
            .input(Blocks.GRAVEL)
            .output(OrePrefix.block, CMaterials.denseClay)
            .tier(4).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()
        registry.builder()
            .input(Blocks.GRAVEL, 4)
            .output(OrePrefix.block, CMaterials.denseClay, 4)
            .tier(4).CEt(ClayEnergy.micro(20)).duration(2)
            .buildAndRegister()
        registry.builder()
            .input(Blocks.GRAVEL, 16)
            .output(OrePrefix.block, CMaterials.denseClay, 16)
            .tier(5).CEt(ClayEnergy.micro(40)).duration(2)
            .buildAndRegister()
        registry.builder()
            .input(Blocks.GRAVEL, 64)
            .output(OrePrefix.block, CMaterials.denseClay, 64)
            .tier(6).CEt(ClayEnergy.micro(80)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(ClayiumBlocks.CLAY_TREE_LOG)
            .output(OrePrefix.dust, CMaterials.advancedIndustrialClay, 16)
            .output(OrePrefix.dust, CMaterials.manganese, 5)
            .output(OrePrefix.dust, CMaterials.lithium, 3)
            .output(OrePrefix.dust, CMaterials.zirconium)
            .tier(6).defaultCEt().duration(400)
            .buildAndRegister()
    }
}