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

object CPanRecipeFactory : IPanRecipeFactory {
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val metaTileEntity = world.getMetaTileEntity(pos)
        if (metaTileEntity is ClayReactorMetaTileEntity) {
            return getEntryClayReactor(metaTileEntity, stacks, laserEnergy, laserCostPerTick)
        }
        val recipe = metaTileEntity
            ?.getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeProvider
            ?.searchRecipe(Int.MAX_VALUE, stacks)
            ?: return null

        return PanRecipe(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * recipe.duration)
    }

    private fun getEntryClayReactor(clayReactor: ClayReactorMetaTileEntity, stacks: List<ItemStack>, laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe? {
        val recipe = clayReactor.workable.recipeProvider.searchRecipe(Int.MAX_VALUE, stacks) ?: return null

        val finalizedDuration = recipe.duration.toDouble() / (laserEnergy + 1.0)
        val laserEnergyCost = laserCostPerTick * finalizedDuration
        return PanRecipe(recipe.inputs, recipe.copyOutputs(), recipe.cePerTick * finalizedDuration + laserEnergyCost)
    }
}