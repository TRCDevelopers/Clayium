package com.github.trcdevelopers.clayium.common.recipe.loader

import com.github.trcdevelopers.clayium.common.clayenergy.ClayEnergy
import com.github.trcdevelopers.clayium.common.recipe.registry.CRecipes
import net.minecraft.block.BlockOldLeaf
import net.minecraft.block.BlockOldLog
import net.minecraft.block.BlockPlanks
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack

object MatterTransformerRecipeLoader {
    fun registerRecipes() {
        // tree variant transformation
        val saplingRecipes = mutableListOf<() -> Unit>()
        val leaveRecipes = mutableListOf<() -> Unit>()
        val logRecipes = mutableListOf<() -> Unit>()
        for (type in BlockPlanks.EnumType.entries) {
            val meta = type.metadata
            val nextType = BlockPlanks.EnumType.byMetadata(meta + 1)
            val nextMeta = nextType.metadata
            // lazy for recipe ordering
            // sapling
            saplingRecipes.add { CRecipes.MATTER_TRANSFORMER.register {
                input(ItemStack(Blocks.SAPLING, 1, meta))
                output(ItemStack(Blocks.SAPLING, 1, nextMeta))
                cePerTick(ClayEnergy.of(1))
                duration(20)
                tier(7)
            } }
            // leaves
            leaveRecipes.add { CRecipes.MATTER_TRANSFORMER.register {
                input(getLeaveStack(type))
                output(getLeaveStack(nextType))
                cePerTick(ClayEnergy.of(1))
                duration(20)
                tier(7)
            } }
            // log
            logRecipes.add { CRecipes.MATTER_TRANSFORMER.register {
                input(getLogStack(type))
                output(getLogStack(nextType))
                cePerTick(ClayEnergy.of(1))
                duration(20)
                tier(7)
            } }
        }
        saplingRecipes.forEach { it() }
        leaveRecipes.forEach { it() }
        logRecipes.forEach { it() }
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
}