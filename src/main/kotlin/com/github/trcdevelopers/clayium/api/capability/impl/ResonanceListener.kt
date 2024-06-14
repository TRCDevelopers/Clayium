package com.github.trcdevelopers.clayium.api.capability.impl

import com.github.trcdevelopers.clayium.api.block.IResonatingBlock
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_RESONANCE
import com.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer

class ResonanceListener(
    metaTileEntity: MetaTileEntity,
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RESONANCE_LISTENER) {
    val range = 2

    var resonance = 1.0
        private set(value) {
            val syncFlag = (metaTileEntity.world?.isRemote == false) && (field != value)
            field = value
            writeCustomData(UPDATE_RESONANCE) {
                writeDouble(value)
            }
        }

    override fun update() {
        if (metaTileEntity.offsetTimer % 20L == 0L) {
            updateResonance()
        }
    }

    private fun updateResonance() {
        var resonance = 1.0
        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val blockPos = metaTileEntity.pos?.add(x, y, z) ?: continue
                    val blockState = metaTileEntity.world?.getBlockState(blockPos) ?: continue
                    val block = blockState.block
                    if (block is IResonatingBlock) {
                        resonance *= block.getResonance(blockState)
                    }
                }
            }
        }

        this.resonance = resonance
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_RESONANCE) {
            resonance = buf.readDouble()
        }
    }
}