package com.github.trcdevelopers.clayium.common.loaders.recipe

import com.github.trcdevelopers.clayium.common.blocks.ClayiumBlocks
import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.ClayiumItems
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

object AssemblerRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.ASSEMBLER

        //region Tools
        registry.builder()
            .input(ClayiumItems.CLAY_ROLLING_PIN)
            .input(ClayiumItems.CLAY_SLICER)
            .output(ClayiumItems.CLAY_IO_CONFIGURATOR)
            .tier(6).CEt(1.0).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(ClayiumItems.CLAY_SPATULA)
            .input(ClayiumItems.CLAY_WRENCH)
            .output(ClayiumItems.CLAY_PIPING_TOOL)
            .tier(6).CEt(1.0).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(OrePrefix.plate, CMaterials.az91d, 3)
            .input(MetaItemClayParts.SynchronousParts, 2)
            .output(ClayiumItems.SYNCHRONIZER)
            .tier(6).duration(20)
            .buildAndRegister()
        //endregion

        registry.builder()
            .input(MetaItemClayParts.ADVANCED_CIRCUIT)
            .input(OrePrefix.plate, CMaterials.industrialClay)
            .output(ClayiumItems.simpleItemFilter)
            .tier(4).CEt(ClayEnergy.micro(80)).duration(20)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.dust, CMaterials.quartz, 16)
            .output(ClayiumBlocks.QUARTZ_CRUCIBLE)
            .CEt(ClayEnergy.milli(10)).duration(20)
            .buildAndRegister()

        //region ClayParts
        for (m in listOf(CMaterials.clay, CMaterials.denseClay)) {
            registry.builder()
                .input(OrePrefix.stick, m, 5)
                .output(OrePrefix.gear, m)
                .tier(3).duration(20)
                .buildAndRegister()
            registry.builder()
                .input(OrePrefix.shortStick, m, 9)
                .output(OrePrefix.gear, m)
                .tier(3).duration(20)
                .buildAndRegister()
            registry.builder()
                .input(OrePrefix.largePlate, m)
                .input(Items.CLAY_BALL, 8)
                .output(OrePrefix.spindle, m)
                .tier(3).duration(20)
                .buildAndRegister()
            registry.builder()
                .input(OrePrefix.largePlate, m)
                .input(OrePrefix.block, m, 8)
                .output(OrePrefix.grindingHead, m)
                .tier(3).duration(20)
                .buildAndRegister()
            registry.builder()
                .input(OrePrefix.largePlate, m)
                .input(OrePrefix.plate, m, 8)
                .output(OrePrefix.cuttingHead, m)
                .tier(3).duration(20)
                .buildAndRegister()
        }
        registry.builder()
            .input(MetaItemClayParts.CEECircuit)
            .input(OrePrefix.plate, CMaterials.industrialClay)
            .output(MetaItemClayParts.CEE)
            .CEt(ClayEnergy.micro(80)).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.PRECISION_CIRCUIT)
            .input(MetaItemClayParts.EnergeticClayDust, 32)
            .output(MetaItemClayParts.INTEGRATED_CIRCUIT)
            .tier(0).CEt(ClayEnergy.milli(100)).duration(1200)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.INTEGRATED_CIRCUIT)
            .input(MetaItemClayParts.CEE)
            .output(MetaItemClayParts.LaserParts)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(MetaItemClayParts.INTEGRATED_CIRCUIT)
            .input(OrePrefix.dust, CMaterials.beryllium, 8)
            .output(MetaItemClayParts.SynchronousParts)
            .tier(6).duration(432_000)
            .buildAndRegister()
        //endregion

        registry.builder()
            .input(Blocks.STONEBRICK)
            .input(Blocks.VINE)
            .output(ItemStack(Blocks.STONEBRICK, 1, 1))
            .tier(6).duration(20)
            .buildAndRegister()
        registry.builder()
            .input(Items.LEATHER, 4)
            .input(Items.STRING, 16)
            .output(Items.SADDLE)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(6000)
            .buildAndRegister()
        registry.builder()
            .input(Items.PAPER, 2)
            .input(Items.STRING, 4)
            .output(Items.NAME_TAG)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(600)
            .buildAndRegister()
    }
}