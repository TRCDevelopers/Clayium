package com.github.trc.clayium.datafix.fixable

import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.datafix.ClayiumDataVersion
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.datafix.IFixableData
import net.minecraftforge.common.util.Constants

class MteHolderMigrator : IFixableData {
    override fun getFixVersion(): Int {
        return ClayiumDataVersion.V1_FILTER_REGISTRY.ordinal
    }

    override fun fixTagCompound(compound: NBTTagCompound): NBTTagCompound {
        if (compound.getString("id") != clayiumId("metaTileEntityHolder").toString()) return compound

        val mteData = compound.getCompoundTag("metaTileEntityData")
        for (facing in EnumFacing.entries) {
            val i = facing.index
            val oldKey = "filterType$i"
            val filterDataKey = "filter$i"
            val newKey = "filterId$i"
            if (mteData.hasKey(oldKey, Constants.NBT.TAG_INT) && mteData.hasKey(filterDataKey, Constants.NBT.TAG_COMPOUND)) {
                mteData.removeTag(oldKey)
                // There is only simpleItemFilter on V0 dataVersion
                mteData.setString(newKey, clayiumId("simple").toString())
            }
        }
        return compound
    }
}