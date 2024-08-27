package com.github.trc.clayium.api.capability

import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.util.takeIfValid
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

// TODO REFACTOR
interface IClayLaserSource {
    val laser: IClayLaser
    val isActive: Boolean
    /**
     * Current laser length of this source.
     */
    val laserLength: Int

    /**
     * updates laser length and target if exists.
     * [previousTarget] must be passed, because if the target is changed, the previous target must be notified.
     *
     * returns the new length, and the new target if it exists.
     * pass runnable to [ifUpdated] to sync values to the client.
     * [ifUpdated] is also called when the laser length is changed.
     */
    fun updateLengthAndTarget(world: World, thisPos: BlockPos, previousTarget: TileEntity?, ifUpdated: (() -> Unit)? = null): Pair<Int, TileEntity?> {
        val newLaserLength = getLaserLength(world, thisPos)
        val targetSide = laser.direction.opposite
        val targetPos = thisPos.offset(laser.direction, newLaserLength)
        val newTarget = world.getTileEntity(targetPos)
        if (newTarget == null) {
            previousTarget.takeIfValid()
                ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, targetSide)
                ?.laserChanged(targetSide, null)
            irradiateLaserBlock(world, targetPos, laser.energy)
            return Pair(newLaserLength, null)
        } else if (previousTarget != newTarget) {
            previousTarget.takeIfValid()
                ?.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, targetSide)
                ?.laserChanged(targetSide, null)
            if (isActive) {
                newTarget.getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, targetSide)
                    ?.laserChanged(targetSide, this.laser)
            }
        } else if (laserLength != newLaserLength) {
            ifUpdated?.invoke()
        }
        return Pair(newLaserLength, newTarget)
    }

    fun getLaserLength(world: IBlockAccess, pos: BlockPos): Int {
        for (i in 1..MAX_LASER_LENGTH) {
            val targetPos = pos.offset(laser.direction, i)
            if (canGoThroughBlock(world, targetPos)) continue
            return i
        }
        return MAX_LASER_LENGTH
    }

    companion object {
        const val MAX_LASER_LENGTH = 32

        private fun canGoThroughBlock(world: IBlockAccess, pos: BlockPos): Boolean {
            val material = world.getBlockState(pos).material
            return (material == Material.AIR) || (material == Material.GLASS)
        }

        private fun irradiateLaserBlock(world: World, targetPos: BlockPos, laserEnergy: Double) {
            val block = world.getBlockState(targetPos).block
            val resultState = if (block === Blocks.SAPLING && laserEnergy > 10000) {
                ClayiumBlocks.CLAY_TREE_SAPLING.defaultState
            } else {
                null
            }
            if (resultState == null) return
            world.destroyBlock(targetPos, false)
            world.setBlockState(targetPos, resultState)
        }
    }
}