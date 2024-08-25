package com.github.trc.clayium.api.pan

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

fun IBlockAccess.isPanCable(pos: BlockPos): Boolean {
    // Forge Multipart support?
    return getBlockState(pos).block is IPanCable || getTileEntity(pos)?.getCapability(ClayiumTileCapabilities.PAN_CABLE, null) != null
}

interface IPanCable {
    companion object {
        val INSTANCE = object : IPanCable {}
    }
}