package com.github.trc.clayium.api.metatileentity.trait

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.capability.ClayiumDataCodecs
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_AREA_RANGE
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.common.blocks.marker.TileClayMarker
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing

class ClayMarkerHandler(
    metaTileEntity: MetaTileEntity
) : MTETrait(metaTileEntity, ClayiumDataCodecs.CLAY_MARKER_HANDLER) {

    /**
     * available after onPlacement.
     */
    var markedRangeAbs: Cuboid6? = null
        private set

    override fun onPlacement() {
        this.markedRangeAbs = this.getRangeFromNeighborMarker()
        writeMarkedRange()
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        writeMarkedRange()
    }

    private fun writeMarkedRange() {
        writeCustomData(UPDATE_AREA_RANGE) {
            val range = markedRangeAbs
            if (range == null) {
                writeBoolean(false)
            } else {
                writeBoolean(true)
                writeCompoundTag(range.writeToNBT(NBTTagCompound()))
            }
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_AREA_RANGE) {
            markedRangeAbs = if (buf.readBoolean()) {
                Cuboid6(buf.readCompoundTag())
            } else {
                null
            }
        }
    }

    /**
     * null if no neighbor marker.
     */
    private fun getRangeFromNeighborMarker(): Cuboid6? {
        val world = this.metaTileEntity.world ?: return null
        if (world.isRemote) return null
        for (side in EnumFacing.entries) {
            val pos = this.metaTileEntity.pos?.offset(side) ?: continue
            val tileEntity = world.getTileEntity(pos)
            if (tileEntity !is TileClayMarker) continue

            val relativeRange = tileEntity.rangeRelative ?: continue
            for (markerPos in tileEntity.markerPoses) {
                world.destroyBlock(markerPos, true)
            }
            return relativeRange.add(pos)
        }
        return null
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = super.serializeNBT()
        val range = this.markedRangeAbs
        if (range != null) {
            data.setTag("markedRange", range.writeToNBT(NBTTagCompound()))
        }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        super.deserializeNBT(data)
        if (data.hasKey("markedRange")) {
            this.markedRangeAbs = Cuboid6(data.getCompoundTag("markedRange"))
        }
    }
}