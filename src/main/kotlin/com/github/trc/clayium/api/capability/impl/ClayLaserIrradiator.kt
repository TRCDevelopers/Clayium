package com.github.trc.clayium.api.capability.impl

import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.laser.ClayLaser
import com.github.trc.clayium.api.metatileentity.interfaces.IWorldObject
import com.github.trc.clayium.common.config.ConfigCore
import com.github.trc.clayium.common.recipe.LaserRecipes
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.lang.ref.WeakReference

class ClayLaserIrradiator(
    val tileEntity: IWorldObject,
) {

    val world get() = tileEntity.worldObj
    val pos get() = tileEntity.position

    private var previousTarget: WeakReference<TileEntity>? = null
    private var lastDirection = EnumFacing.NORTH

    /**
     * to memorize how much energy irradiated to block
     * won't be used if target was TileEntity
     */
    private var previousTargetPos: BlockPos? = null
    private var totalEnergyIrradiated: Double = 0.0

    private fun TileEntity.getLaserAcceptor(targetSide: EnumFacing) =
        getCapability(ClayiumTileCapabilities.CLAY_LASER_ACCEPTOR, targetSide)

    /**
     * returns laser length.
     */
    fun irradiateLaser(direction: EnumFacing, laser: ClayLaser): Int {
        val world = this.world ?: return 0
        val pos = this.pos ?: return 0
        if (world.isRemote) return 0
        val length = getLaserLength(direction)
        val targetPos = pos.offset(direction, length)
        val targetSide = direction.opposite

        val newTarget = world.getTileEntity(targetPos)
        val previousTarget = this.previousTarget?.get()
        if (newTarget === previousTarget) {
            newTarget?.getLaserAcceptor(targetSide)?.acceptLaser(targetSide, laser)
        } else {
            newTarget?.getLaserAcceptor(targetSide)?.acceptLaser(targetSide, laser)
            previousTarget?.getLaserAcceptor(targetSide)?.acceptLaser(targetSide, null)
            this.previousTarget = newTarget?.let { WeakReference(it) }
        }
        if (newTarget == null) {
            // no tile entity target so irradiate the block
            irradiateLaserBlock(laser.energy, targetPos)
        }
        this.lastDirection = direction
        return length
    }

    fun stopIrradiation() {
        val targetSide = lastDirection.opposite
        this.previousTarget?.get()?.getLaserAcceptor(targetSide)
            ?.acceptLaser(targetSide, null)
    }

    private fun getLaserLength(direction: EnumFacing): Int {
        val pos = this.pos ?: return 0
        val targetPos = BlockPos.MutableBlockPos().setPos(pos)
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
        if (previousTargetPos != targetPos) {
            totalEnergyIrradiated = 0.0
            previousTargetPos = targetPos
        }
        totalEnergyIrradiated += energy
        val recipe = LaserRecipes.LASER.getRecipe(block, energy)
        val resultState = if (recipe != null && recipe.isSufficient(totalEnergyIrradiated)) {
            recipe.output.defaultState
        } else {
            null
        }
        if (resultState == null) return
        world.destroyBlock(targetPos, false)
        world.setBlockState(targetPos, resultState)
    }
}