package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.laser.IClayLaser
import com.github.trc.clayium.api.metatileentity.interfaces.IWorldObject
import com.github.trc.clayium.api.util.TileEntityAccess
import com.github.trc.clayium.common.blocks.ClayiumBlocks
import com.github.trc.clayium.common.config.ConfigCore
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ClayLaserSource(
    val tileEntity: IWorldObject,
) {

    val world get() = tileEntity.world
    val pos get() = tileEntity.pos

    private var previousTarget: TileEntityAccess? = null

    private fun TileEntity.getLaserAcceptor(targetSide: EnumFacing) =
        getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, targetSide)

    /**
     * returns laser length.
     */
    fun irradiateLaser(direction: EnumFacing, laser: IClayLaser): Int {
        val world = this.world ?: return 0
        val pos = this.pos ?: return 0
        val length = getLaserLength(direction)
        val targetPos = pos.offset(direction, length)
        val targetSide = direction.opposite

        val newTarget = world.getTileEntity(targetPos)
        val previousTarget = this.previousTarget?.get()
        if (newTarget != previousTarget) {
            newTarget?.getLaserAcceptor(targetSide)?.laserChanged(targetSide, laser)
            previousTarget?.getLaserAcceptor(targetSide)?.laserChanged(targetSide, null)
        }
        if (newTarget == null) {
            // no tile entity target so irradiate the block
            irradiateLaserBlock(laser.energy, targetPos)
        } else {
            this.previousTarget = TileEntityAccess(newTarget)
        }
        return length
    }

    fun stopIrradiation(direction: EnumFacing) {
        val targetSide = direction.opposite
        this.previousTarget?.get()?.getLaserAcceptor(targetSide)
            ?.laserChanged(targetSide, null)
    }

    private fun getLaserLength(direction: EnumFacing): Int {
        val targetPos = BlockPos.MutableBlockPos()
        val maxLaserLength = ConfigCore.misc.maxClayLaserLength
        for (i in 1..maxLaserLength) {
            val targetPos = targetPos.move(direction)
            if (canGoThroughBlock(targetPos)) continue
            return i
        }
        return maxLaserLength
    }

    private fun canGoThroughBlock(pos: BlockPos): Boolean {
        val material = world?.getBlockState(pos)?.material
        return (material == Material.AIR) || (material == Material.GLASS)
    }

    private fun irradiateLaserBlock(energy: Double, targetPos: BlockPos) {
        val world = world ?: return
        val block = world.getBlockState(targetPos).block
        val resultState = if (block === Blocks.SAPLING && energy > 10000) {
            ClayiumBlocks.CLAY_TREE_SAPLING.defaultState
        } else {
            null
        }
        if (resultState == null) return
        world.destroyBlock(targetPos, false)
        world.setBlockState(targetPos, resultState)
    }
}