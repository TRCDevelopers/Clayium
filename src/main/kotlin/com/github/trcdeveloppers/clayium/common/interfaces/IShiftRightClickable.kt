package com.github.trcdeveloppers.clayium.common.interfaces

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IShiftRightClickable {

    /**
     * Called when the block is right-clicked while the player is sneaking.
     */
    fun onShiftRightClicked(
        world: World,
        pos: BlockPos,
        state: IBlockState,
        player: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing
    ): Boolean
}