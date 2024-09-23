package com.github.trc.clayium.api.capability.impl

import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.value.sync.SyncHandlers
import com.github.trc.clayium.api.block.IResonatingBlock
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_RESONANCE
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import net.minecraft.network.PacketBuffer

class ResonanceManager(
    metaTileEntity: MetaTileEntity,
    private val range: Int
) : MTETrait(metaTileEntity, ClayiumDataCodecs.RESONANCE_LISTENER) {

    var resonance = 1.0
        private set(value) {
            val v = value.coerceAtMost(Long.MAX_VALUE.toDouble())
            val syncFlag = (!metaTileEntity.isRemote) && (field != v)
            field = v
            if (syncFlag) {
                writeCustomData(UPDATE_RESONANCE) {
                    writeDouble(v)
                }
            }
        }

    override fun update() {
        if (!metaTileEntity.isRemote && metaTileEntity.offsetTimer % 20L == 0L) {
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

    fun sync(syncManager: GuiSyncManager) {
        syncManager.syncValue("resonance", SyncHandlers.doubleNumber(::resonance, ::resonance::set))
    }
}