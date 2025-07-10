package com.github.trc.clayium.common.pan.factories

import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.metatileentity.multiblock.ClayReactorMetaTileEntity
import com.github.trc.clayium.api.pan.IPanRecipe
import com.github.trc.clayium.api.pan.IPanRecipeFactory
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.pan.PanRecipe
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import kotlin.math.ceil

object CPanRecipeFactory : IPanRecipeFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return null

        val multiblockCapability = metaTileEntity.getCapability(ClayiumTileCapabilities.MULTIBLOCK, null)
        var machineTier = metaTileEntity.tier.numeric

        if (multiblockCapability != null) {
            if (!multiblockCapability.structureFormed) return null
            if (metaTileEntity is ClayReactorMetaTileEntity) return getEntryClayReactor(metaTileEntity, stacks, laserEnergy, laserCostPerTick)
            machineTier = multiblockCapability.recipeLogicTier
        }
        val recipe = metaTileEntity
            .getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeProvider
            ?.searchRecipe(machineTier, stacks)
            ?: return null

        return PanRecipe(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * recipe.duration)
    }

    private fun getEntryClayReactor(clayReactor: ClayReactorMetaTileEntity, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val recipe = clayReactor.workable.recipeProvider.searchRecipe(Int.MAX_VALUE, stacks) ?: return null

        // TODO: Ignore white laser
        val finalizedDuration = ceil(recipe.duration.toDouble() / (laserEnergy + 1.0)).toLong()
        return PanRecipe(recipe.inputs, recipe.copyOutputs(), (recipe.cePerTick + laserCostPerTick) * finalizedDuration)
    }
}