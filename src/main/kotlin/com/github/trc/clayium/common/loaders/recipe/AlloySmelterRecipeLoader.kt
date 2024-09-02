package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMarkerMaterials
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.util.Mods
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object AlloySmelterRecipeLoader {
    fun registerRecipes() {
        val registry = CRecipes.ALLOY_SMELTER
        val ingotDust = arrayOf(OrePrefix.ingot, OrePrefix.dust)
        simpleAlloy()
            .input(ingotDust, CMaterials.copper, 3)
            .input(ingotDust, CMaterials.tin)
            .output(OrePrefix.ingot, CMaterials.bronze, 4)
            .buildAndRegister()
        simpleAlloy()
            .input(ingotDust, CMaterials.copper, 3)
            .input(ingotDust, CMaterials.zinc, 1)
            .output(OrePrefix.ingot, CMaterials.brass, 4)
            .buildAndRegister()
        simpleAlloy()
            .input(ingotDust, CMaterials.iron, 2)
            .input(ingotDust, CMaterials.nickel, 1)
            .output(OrePrefix.ingot, CMaterials.invar, 3)
            .buildAndRegister()
        simpleAlloy()
            .input(ingotDust, CMaterials.gold)
            .input(ingotDust, CMaterials.silver)
            .output(OrePrefix.ingot, CMaterials.electrum, 2)
            .buildAndRegister()

        registry.builder()
            .input(ingotDust, CMaterials.zinc, 9)
            .input(ingotDust, CMaterials.aluminum)
            .output(OrePrefix.ingot, CMaterials.zinc_aluminum, 10)
            .tier(6).CEt(ClayEnergy.milli(100)).duration(50)
            .buildAndRegister()
        registry.builder()
            .input(ingotDust, CMaterials.zinc, 9)
            .input(ingotDust, CMaterials.zirconium)
            .output(OrePrefix.ingot, CMaterials.zinc_zirconium, 10)
            .tier(6).CEt(ClayEnergy.of(3)).duration(50)
            .buildAndRegister()

        registry.builder()
            .input(ingotDust, CMaterials.magnesium, 9)
            .input(ingotDust, CMaterials.zinc_aluminum)
            .output(OrePrefix.ingot, CMaterials.az91d, 10)
            .tier(6).CEt(ClayEnergy.of(1)).duration(500)
            .buildAndRegister()
        registry.builder()
            .input(ingotDust, CMaterials.magnesium, 19)
            .input(ingotDust, CMaterials.zinc_zirconium)
            .output(OrePrefix.ingot, CMaterials.zk60a, 20)
            .tier(6).CEt(ClayEnergy.of(3)).duration(500)
            .buildAndRegister()
        if (Mods.EnderIO.isModLoaded) {
            /* Redstone Alloy */
            registry.builder()
                .input(Items.REDSTONE)
                .input(OrePrefix.item, CMaterials.silicon)
                .output(OrePrefix.ingot, CMarkerMaterials.redstoneAlloy)
                .tier(6).defaultCEt().duration(100)
                .buildAndRegister()
            /* Conductive Iron */
            registry.builder()
                .input(Items.REDSTONE)
                .input(OrePrefix.ingot, CMaterials.iron)
                .output(OrePrefix.ingot, CMarkerMaterials.conductiveIron)
                .tier(6).defaultCEt().duration(100)
                .buildAndRegister()
            registry.builder()
                .input(Items.REDSTONE)
                .input(OrePrefix.dust, CMaterials.iron)
                .output(OrePrefix.ingot, CMarkerMaterials.conductiveIron)
                .tier(6).defaultCEt().duration(100)
                .buildAndRegister()
        }
    }

    private fun simpleAlloy() = CRecipes.ALLOY_SMELTER.builder()
        .tier(0).CEt(ClayEnergy.milli(1)).duration(40)
}