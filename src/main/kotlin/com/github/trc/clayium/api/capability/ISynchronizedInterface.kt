package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

interface ISynchronizedInterface {

    val target: MetaTileEntity?

    /**
     * should be synced to the client because it is used for rendering target position.
     * null if no target.
     */
    val targetPos: BlockPos?

    /**
     * should be synced to the client since it is used for rendering target dimension name.
     */
    val targetDimensionId: Int

    /**
     * the ItemStack form of the target block.
     * only used for rendering. not used on the server.
     */
    val targetItemStack: ItemStack

    /**
     * synchronize this block to given pos of given dimension.
     *
     * @return true if the synchronization was successful
     */
    fun synchronize(pos: BlockPos, dimensionId: Int): Boolean
}