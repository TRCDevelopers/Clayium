package com.github.trc.clayium.common.pan.factories

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
    override fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>, laserEnergy: Double): IPanRecipe? {
        val metaTileEntity = world.getMetaTileEntity(pos)
        val recipe = metaTileEntity
            ?.getCapability(ClayiumTileCapabilities.RECIPE_LOGIC, null)
            ?.recipeRegistry
            ?.findRecipe(Int.MAX_VALUE, stacks)
            ?: return null

        val energyCost = if (metaTileEntity is ClayReactorMetaTileEntity) {
            recipe.cePerTick * (recipe.duration / laserEnergy)
        } else {
            recipe.cePerTick * recipe.duration
        }

        return PanRecipe(recipe.inputs, recipe.copyOutputs(), energyCost)
    }

}