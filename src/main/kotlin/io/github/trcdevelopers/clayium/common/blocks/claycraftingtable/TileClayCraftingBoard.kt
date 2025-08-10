package io.github.trcdevelopers.clayium.common.blocks.claycraftingtable

import io.github.trcdevelopers.clayium.api.capability.impl.ClayiumItemStackHandler
import io.github.trcdevelopers.clayium.api.metatileentity.interfaces.IMarkDirty
import io.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity

class TileClayCraftingBoard : TileEntity(), IMarkDirty {

    val inventory = ClayiumItemStackHandler(this, 9)

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        val data = super.writeToNBT(compound)
        CUtils.writeItems(inventory, "inventory", data)
        return data
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        CUtils.readItems(inventory, "inventory", compound)
    }

    override fun markAsDirty() = this.markDirty()
}