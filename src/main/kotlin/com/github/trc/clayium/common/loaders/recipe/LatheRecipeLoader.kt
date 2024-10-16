package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials.clay
import com.github.trc.clayium.api.unification.material.CMaterials.denseClay
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.block
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.cylinder
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.disc
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.needle
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.ring
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.shortStick
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.smallDisc
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.smallRing
import com.github.trc.clayium.api.unification.ore.OrePrefix.Companion.stick
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object LatheRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.LATHE

        registry.builder()
            .input(Items.CLAY_BALL)
            .output(shortStick, clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(MetaItemClayParts.LargeClayBall)
            .output(cylinder, clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(cylinder, clay)
            .output(needle, clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(needle, clay)
            .output(stick, clay, 6)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(3)
            .buildAndRegister()

        registry.builder()
            .input(disc, clay)
            .output(ring, clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

        registry.builder()
            .input(smallDisc, clay)
            .output(smallRing, clay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(1)
            .buildAndRegister()

        registry.builder()
            .input(block, denseClay, 2)
            .output(cylinder, denseClay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(cylinder, denseClay)
            .output(needle, denseClay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(needle, denseClay)
            .output(stick, denseClay, 6)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(6)
            .buildAndRegister()

        registry.builder()
            .input(disc, denseClay)
            .output(ring, denseClay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(4)
            .buildAndRegister()

        registry.builder()
            .input(smallDisc, denseClay)
            .output(smallRing, denseClay)
            .tier(0).CEt(ClayEnergy.micro(10)).duration(2)
            .buildAndRegister()

    }
}