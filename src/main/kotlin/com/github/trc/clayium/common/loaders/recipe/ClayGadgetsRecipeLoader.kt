package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.blocks.BlockCaReactorCoil
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.items.ClayiumItems
import com.github.trc.clayium.common.items.metaitem.MetaItemClayGadget
import com.github.trc.clayium.common.items.metaitem.MetaItemClayParts
import com.github.trc.clayium.common.recipe.RecipeUtils
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.init.Items

object ClayGadgetsRecipeLoader {
    fun registerRecipes() {
        val asm = CRecipes.ASSEMBLER

        asm.builder()
            .input(Items.LEATHER, 4)
            .input(OrePrefix.plate, CMaterials.az91d, 8)
            .output(ClayiumItems.CLAY_GADGET_HOLDER)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
            .buildAndRegister()

        //region overclockers
        asm.builder()
            .input(OrePrefix.dust, CMaterials.industrialClay, 8)
            .input(OrePrefix.plate, CMaterials.az91d, 4)
            .output(MetaItemClayParts.ClayGadgetParts)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayParts.ClayGadgetParts)
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.ANTIMATTER))
            .output(MetaItemClayGadget.OverclockMk1)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.OverclockMk1)
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.PURE_ANTIMATTER))
            .output(MetaItemClayGadget.OverclockMk2)
            .tier(10).CEt(ClayEnergy.of(10_000)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.OverclockMk2)
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.OEC))
            .output(MetaItemClayGadget.OverclockMk3)
            .tier(10).CEt(ClayEnergy.of(100_000)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.OverclockMk3)
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.OPA))
            .output(MetaItemClayGadget.OverclockMk4)
            .tier(10).CEt(ClayEnergy.of(1_000_000)).duration(120)
            .buildAndRegister()
        //endregion

        //region Flight
        asm.builder()
            .input(MetaItemClayParts.ClayGadgetParts)
            .input(MetaItemClayParts.ClayAnima, 16)
            .output(MetaItemClayGadget.FlightMk1)
            .tier(10).CEt(100_000).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.FlightMk1)
            .input(MetaItemClayParts.ClayPsyche, 16)
            .output(MetaItemClayGadget.FlightMk2)
            .tier(10).CEt(1_000_000).duration(1200)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.FlightMk2)
            .input(ClayiumBlocks.OVERCLOCKER.getItem(BlockCaReactorCoil.BlockType.OPA))
            .output(MetaItemClayGadget.FlightMk3)
            .tier(10).CEt(10_000_000).duration(12000)
            .buildAndRegister()
        //endregion

        //region Health
        asm.builder()
            .input(MetaItemClayParts.ClayGadgetParts)
            .input(MetaItemClayParts.PrecisionCircuit, 4)
            .output(MetaItemClayGadget.HealthMk1)
            .tier(4).CEt(ClayEnergy.milli(100)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.HealthMk1)
            .input(MetaItemClayParts.ClaySpirit, 4)
            .output(MetaItemClayGadget.HealthMk2)
            .tier(4).CEt(ClayEnergy.of(1000)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.HealthMk2)
            .input(MetaItemClayParts.ClayAnima, 4)
            .output(MetaItemClayGadget.HealthMk3)
            .tier(10).CEt(ClayEnergy.of(100_000)).duration(120)
            .buildAndRegister()
        //endregion

        // region AutoEat
        asm.builder()
            .input(MetaItemClayParts.ClayGadgetParts)
            .input(MetaItemClayParts.IntegratedCircuit, 2)
            .output(MetaItemClayGadget.AutoEatEconomical)
            .tier(4).CEt(ClayEnergy.of(1)).duration(120)
            .buildAndRegister()
        RecipeUtils.addShapelessRecipe("clay_gadget_auto_eat",
            MetaItemClayGadget.AutoEat.getStackForm(),
            MetaItemClayGadget.AutoEatEconomical,
        )
        RecipeUtils.addShapelessRecipe("clay_gadget_auto_eat_thrifty",
            MetaItemClayGadget.AutoEatEconomical.getStackForm(),
            MetaItemClayGadget.AutoEat,
        )
        //endregion

        asm.builder()
            .input(MetaItemClayGadget.OverclockMk1)
            .input(MetaItemClayParts.ClaySpirit, 4)
            .output(MetaItemClayGadget.RepeatedlyAttack)
            .tier(10).CEt(ClayEnergy.of(1000)).duration(120)
            .buildAndRegister()

        //region LongArm
        asm.builder()
            .input(MetaItemClayParts.ClayGadgetParts)
            .input(MetaItemClayParts.ManipulatorMk1)
            .output(MetaItemClayGadget.LongArmMk1)
            .tier(4).CEt(ClayEnergy.milli(1)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.LongArmMk1)
            .input(MetaItemClayParts.ManipulatorMk2)
            .output(MetaItemClayGadget.LongArmMk2)
            .tier(4).CEt(ClayEnergy.of(10)).duration(120)
            .buildAndRegister()
        asm.builder()
            .input(MetaItemClayGadget.LongArmMk2)
            .input(MetaItemClayParts.ManipulatorMk3)
            .output(MetaItemClayGadget.LongArmMk3)
            .tier(4).CEt(ClayEnergy.of(100_000)).duration(120)
            .buildAndRegister()
        //endregion
    }
}