package com.github.trc.clayium.api.metatileentity.trait

import com.github.trc.clayium.api.block.IOverclockerBlock
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import kotlin.math.min

class OverclockHandler(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.OVERCLOCK_HANDLER) {

    var rawOcFactor: Double = 1.0
        private set(value) {
            val syncFlag = !metaTileEntity.isRemote && field != value
            field = value
            if (syncFlag) {
                writeCustomData(ClayiumDataCodecs.UPDATE_OC_FACTOR) {
                    writeDouble(rawOcFactor)
                }
            }
            accelerationFactor = min(rawOcFactor, 10.0)
            compensatedFactor = if (rawOcFactor > 10.0) rawOcFactor / accelerationFactor else 1.0
        }

    var accelerationFactor = 1.0
        private set
    var compensatedFactor = 1.0
        private set

    override fun onFirstTick() {
        super.onFirstTick()
        rawOcFactor = getOcFactor()
    }

    fun onNeighborBlockChange() {
        rawOcFactor = getOcFactor()
    }

    private fun getOcFactor(): Double {
        var value = 1.0
        val world = metaTileEntity.world ?: return value
        val pos = metaTileEntity.pos ?: return value
        for (side in EnumFacing.entries) {
            val neighborState = metaTileEntity.getNeighborBlockState(side) ?: continue
            val neighboringBlock = neighborState.block
            if (neighboringBlock is IOverclockerBlock) {
                value *= (neighboringBlock as IOverclockerBlock).getOverclockFactor(world, pos.offset(side))
            }
        }
        return value
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == ClayiumDataCodecs.UPDATE_OC_FACTOR) {
            rawOcFactor = buf.readDouble()
        }
    }
}