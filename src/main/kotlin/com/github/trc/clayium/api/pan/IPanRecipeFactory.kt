package com.github.trc.clayium.api.pan

import com.github.trc.clayium.api.ClayEnergy
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * Implement this in [net.minecraft.block.Block] to provide a Recipe for PAN.
 */
interface IPanRecipeFactory {
    /**
     * This method is called from a PAN Adapter that this block is neighboring to.
     * @param world the world that the PAN Adapter is in.
     * @param pos the position of this block.
     * @param stacks the inventory of the PAN Adapter.
     */
    fun getEntry(world: IBlockAccess, pos: BlockPos, stacks: List<ItemStack>,
                 laserEnergy: Double, laserCostPerTick: ClayEnergy): IPanRecipe?
}