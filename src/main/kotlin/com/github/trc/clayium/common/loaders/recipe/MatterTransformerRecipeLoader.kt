package com.github.trc.clayium.common.loaders.recipe

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.unification.material.CMaterials
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.common.recipe.registry.CRecipes
import net.minecraft.block.BlockOldLeaf
import net.minecraft.block.BlockOldLog
import net.minecraft.block.BlockPlanks
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

object MatterTransformerRecipeLoader {
    fun registerRecipes() {
        for (type in BlockPlanks.EnumType.entries) {
            val meta = type.metadata
            val nextType = BlockPlanks.EnumType.byMetadata(meta + 1)
            val nextMeta = nextType.metadata
            // lazy for recipe ordering
            // sapling
            CRecipes.MATTER_TRANSFORMER.register {
                input(ItemStack(Blocks.SAPLING, 1, meta))
                output(ItemStack(Blocks.SAPLING, 1, nextMeta))
                CEt(ClayEnergy.of(1))
                duration(20)
                tier(7)
            }
            // leaves
            CRecipes.MATTER_TRANSFORMER.register {
                input(getLeaveStack(type))
                output(getLeaveStack(nextType))
                CEt(ClayEnergy.of(1))
                duration(20)
                tier(7)
            }
            // log
            CRecipes.MATTER_TRANSFORMER.register {
                input(getLogStack(type))
                output(getLogStack(nextType))
                CEt(ClayEnergy.of(1))
                duration(20)
                tier(7)
            }
        }

        registerMaterialTransformations()
    }

    private fun getLeaveStack(type: BlockPlanks.EnumType): ItemStack {
        val meta = type.metadata
        val block = if (type in BlockOldLeaf.VARIANT.allowedValues) Blocks.LEAVES else Blocks.LEAVES2
        val actualMeta = if (type in BlockOldLeaf.VARIANT.allowedValues) meta else meta - 4
        return ItemStack(block, 1, actualMeta)
    }

    private fun getLogStack(type: BlockPlanks.EnumType): ItemStack {
        val meta = type.metadata
        val block = if (type in BlockOldLog.VARIANT.allowedValues) Blocks.LOG else Blocks.LOG2
        val actualMeta = if (type in BlockOldLog.VARIANT.allowedValues) meta else meta - 4
        return ItemStack(block, 1, actualMeta)
    }

    private fun registerMaterialTransformations() {
        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.lithium)
            .output(CMaterials.sodium)
            .tier(7).CEtFactor(10.0).duration(200)

            .chain(CMaterials.potassium).CEtFactor(30.0)
            .chain(CMaterials.rubidium).tier(8).CEtFactor(10.0)
            .chain(CMaterials.caesium).CEtFactor(20.0)
            .chain(CMaterials.francium).CEtFactor(30.0)
            .chain(CMaterials.radium).CEtFactor(50.0)
            .chain(CMaterials.actinium).tier(9).CEtFactor(10.0)
            .chain(CMaterials.thorium).CEtFactor(20.0)
            .chain(CMaterials.protactinium).CEtFactor(30.0)
            .chain(CMaterials.uranium).CEtFactor(50.0)
            .chain(CMaterials.neptunium).CEtFactor(80.0)
            .chain(CMaterials.plutonium).tier(10).CEtFactor(20.0)
            .chain(CMaterials.americium).tier(11).CEtFactor(30.0)
            .chain(CMaterials.curium).tier(12).CEtFactor(50.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.beryllium)
            .output(CMaterials.magnesium)
            .tier(7).CEtFactor(10.0).duration(200)

            .chain(CMaterials.calcium).CEtFactor(20.0)
            .chain(CMaterials.strontium).CEtFactor(30.0)
            .chain(CMaterials.barium).CEtFactor(50.0)
            .chain(CMaterials.lanthanum).tier(8).CEtFactor(10.0)
            .chain(CMaterials.cerium).CEtFactor(30.0)
            .chain(CMaterials.praseodymium).CEtFactor(90.0)
            .chain(CMaterials.neodymium).tier(9).CEtFactor(20.0)
            .chain(CMaterials.promethium).tier(10).CEtFactor(10.0)
            .chain(CMaterials.samarium).tier(11).CEtFactor(20.0)
            .chain(CMaterials.europium).tier(12).CEtFactor(60.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.zirconium)
            .output(CMaterials.titanium)
            .tier(8).CEtFactor(60.0).duration(200)
            .chain(CMaterials.vanadium).tier(9).CEtFactor(60.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.manganese)
            .output(CMaterials.iron)
            .tier(7).CEtFactor(90.0).duration(200)
            .chain(CMaterials.cobalt).tier(8).CEtFactor(30.0)
            .chain(CMaterials.nickel).CEtFactor(80.0)
            .chain(CMaterials.palladium).tier(9).CEtFactor(40.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.zinc)
            .output(CMaterials.copper)
            .tier(8).CEtFactor(20.0).duration(200)
            .chain(CMaterials.silver).tier(9).CEtFactor(10.0)
            .chain(CMaterials.gold).tier(9).CEtFactor(50.0)
            .chain(CMaterials.palladium).tier(10).CEtFactor(30.0)
            .chain(CMaterials.iridium).tier(11).CEtFactor(10.0)
            .chain(CMaterials.osmium).tier(11).CEtFactor(30.0)
            .chain(CMaterials.rhenium).tier(12).CEtFactor(10.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.hafnium)
            .output(CMaterials.tantalum)
            .tier(8).CEtFactor(70.0).duration(200)
            .chain(CMaterials.tungsten).tier(9).CEtFactor(40.0)
            .chain(CMaterials.molybdenum).tier(10).CEtFactor(20.0)
            .chain(CMaterials.chromium).tier(11).CEtFactor(10.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.ingot)
            .input(CMaterials.lead)
            .output(CMaterials.tin)
            .tier(7).CEtFactor(50.0).duration(200)
            .chain(CMaterials.antimony).tier(8).CEtFactor(20.0)
            .chain(CMaterials.bismuth).tier(9).CEtFactor(10.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.dust)
            .input(CMaterials.silicon)
            .output(CMaterials.phosphorus)
            .tier(7).CEtFactor(10.0).duration(200)
            .chain(CMaterials.sulfur).CEtFactor(30.0)
            .buildAndRegister()

        CRecipes.MATTER_TRANSFORMER.builder()
            .defaultPrefix(OrePrefix.dust)
            .input(CMaterials.industrialClay)
            .output(CMaterials.carbon)
            .CEt(ClayEnergy.of(1)).tier(7)
            .duration(200)
            .chain(CMaterials.graphite)
            .chain(CMaterials.charcoal)
            .chain(CMaterials.coal)
            .chain(CMaterials.lapis)
            .buildAndRegister()
    }
}