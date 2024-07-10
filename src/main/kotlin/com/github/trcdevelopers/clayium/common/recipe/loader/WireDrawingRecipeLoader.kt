package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object WireDrawingRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.WIRE_DRAWING_MACHINE

        registry.builder()
            .input(Items.CLAY_BALL)
            .output(MetaItemClayParts.CLAY_STICK)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_PIPE)
            .output(MetaItemClayParts.CLAY_STICK, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_CYLINDER)
            .output(MetaItemClayParts.CLAY_STICK, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.CLAY_SMALL_DISC)
            .output(MetaItemClayParts.CLAY_STICK)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_SMALL_DISC)
            .output(MetaItemClayParts.DENSE_CLAY_STICK)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_PIPE)
            .output(MetaItemClayParts.DENSE_CLAY_STICK, 4)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.DENSE_CLAY_CYLINDER)
            .output(MetaItemClayParts.DENSE_CLAY_STICK, 8)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()
    }
}