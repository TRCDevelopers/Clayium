package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import com.github.trcdevelopers.clayium.common.unification.material.CMaterials
import com.github.trcdevelopers.clayium.common.unification.ore.OrePrefix
import net.minecraft.init.Items

object LatheRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.LATHE

        registry.builder()
            .input(Items.CLAY_BALL)
            .output(MetaItemClayParts.CLAY_SHORT_STICK)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.LARGE_CLAY_BALL)
            .output(MetaItemClayParts.CLAY_CYLINDER)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CYLINDER)
            .output(MetaItemClayParts.CLAY_NEEDLE)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_NEEDLE)
            .output(MetaItemClayParts.CLAY_STICK, 6)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_DISC)
            .output(MetaItemClayParts.CLAY_RING)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_SMALL_DISC)
            .output(MetaItemClayParts.CLAY_SMALL_RING)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(OrePrefix.block, CMaterials.denseClay, 2)
            .output(MetaItemClayParts.DENSE_CLAY_CYLINDER)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_CYLINDER)
            .output(MetaItemClayParts.DENSE_CLAY_NEEDLE)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_NEEDLE)
            .output(MetaItemClayParts.DENSE_CLAY_STICK, 6)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_DISC)
            .output(MetaItemClayParts.DENSE_CLAY_RING)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_SMALL_DISC)
            .output(MetaItemClayParts.DENSE_CLAY_SMALL_RING)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

    }
}