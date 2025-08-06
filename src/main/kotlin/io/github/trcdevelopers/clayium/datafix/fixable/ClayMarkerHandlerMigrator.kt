package io.github.trcdevelopers.clayium.datafix.fixable

import codechicken.lib.vec.Cuboid6
import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.datafix.ClayiumDataVersion
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.datafix.IFixableData

class ClayMarkerHandlerMigrator : IFixableData {


    override fun getFixVersion(): Int {
        return ClayiumDataVersion.V2_RANGED_MACHINES_CHANGE_FORMAT.ordinal
    }

    override fun fixTagCompound(compound: NBTTagCompound): NBTTagCompound {
        if (compound.getString("id") != clayiumId("metaTileEntityHolder").toString()) return compound
        val metaTileEntityNbt = compound.getCompoundTag("metaTileEntityData")
        val clayMarkerHandlerNbt = metaTileEntityNbt.getCompoundTag(ClayiumDataCodecs.CLAY_MARKER_HANDLER)
        val oldData = clayMarkerHandlerNbt.getCompoundTag(OLD_TAG_NAME)
        val cuboid6 = Cuboid6(oldData)
        val minPos = cuboid6.min.pos()
        val maxPos = cuboid6.max.pos().add(-1, -1, -1)
        clayMarkerHandlerNbt.removeTag(OLD_TAG_NAME)
        clayMarkerHandlerNbt.setLong("minPos", minPos.toLong())
        clayMarkerHandlerNbt.setLong("maxPos", maxPos.toLong())

        metaTileEntityNbt.setTag(ClayiumDataCodecs.CLAY_MARKER_HANDLER, clayMarkerHandlerNbt)
        return compound
    }

    companion object {
        const val OLD_TAG_NAME = "markedRange"
    }
}