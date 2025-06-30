package com.github.trc.clayium.common.capability.impl

import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER
import com.github.trc.clayium.api.capability.ClayiumTileCapabilities
import com.github.trc.clayium.api.capability.IItemFilter
import com.github.trc.clayium.api.capability.IItemFilterApplicatable
import com.github.trc.clayium.api.metatileentity.MTETrait
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CLog
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.items.filter.ItemFilterBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.registry.ForgeRegistries

class ItemFilterHolderTrait(mte: MetaTileEntity) : MTETrait(mte, clayiumId("item_filter_holder").toString()), IItemFilterApplicatable {

    private val filters = MutableList<Pair<IItemFilter, ItemFilterBase>?>(6) { null }
    private val clientFilterFlags = BooleanArray(6) { false }

    override fun setFilter(side: EnumFacing, filter: IItemFilter, filterItem: ItemFilterBase) {
        filters[side.index] = Pair(filter, filterItem)
        metaTileEntity.markDirty()
        writeCustomData(UPDATE_FILTER) {
            writeVarInt(side.index)
            writeBoolean(true)
        }
    }

    override fun getFilter(side: EnumFacing): IItemFilter? {
        return filters[side.index]?.first
    }

    override fun getFilterItem(side: EnumFacing): ItemFilterBase? {
        val i = side.index
        return filters[side.index]?.second
    }

    override fun clearFilter(side: EnumFacing) {
        filters[side.index] = null
        writeCustomData(UPDATE_FILTER) {
            writeVarInt(side.index)
            writeBoolean(false)
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == UPDATE_FILTER) {
            clientFilterFlags[buf.readVarInt()] = buf.readBoolean()
            metaTileEntity.scheduleRenderUpdate()
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        for ((i, p) in filters.withIndex()) {
            buf.writeBoolean(p != null)
        }
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        for (i in 0..<6) {
            clientFilterFlags[i] = buf.readBoolean()
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        val data = NBTTagCompound()
        for ((i, p) in filters.withIndex()) {
            if (p == null) continue
            val filter = p.first
            val filterItem = p.second
            val filterRegName = filterItem.registryName ?: continue
            data.setTag("filter$i", filter.serializeNBT())
            data.setString("filterItemId$i", filterRegName.toString())
        }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        for (i in 0..<6) {
            if (!(data.hasKey("filter$i", Constants.NBT.TAG_COMPOUND) && data.hasKey("filterItemId$i", Constants.NBT.TAG_STRING))) {
                continue
            }
            val filterItemRegistryName = data.getString("filterItemId$i")
            val filterItem = ForgeRegistries.ITEMS.getValue(ResourceLocation(filterItemRegistryName))
            if (filterItem == null) {
                CLog.warn("Item Filter $filterItemRegistryName not found. pos: ${metaTileEntity.pos}, side: ${EnumFacing.byIndex(i)}")
                continue
            } else if (filterItem !is ItemFilterBase) {
                CLog.warn("Item Filter is corrupted. id: $filterItemRegistryName, pos: ${metaTileEntity.pos}, side: ${EnumFacing.byIndex(i)}")
                continue
            }
            val filter = filterItem.createItemFilter()
            filter.deserializeNBT(data.getCompoundTag("filter$i"))
            filters[i] = Pair(filter, filterItem)
        }
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumTileCapabilities.ITEM_FILTER_APPLICATABLE) {
            return capability.cast(this)
        }
        return super.getCapability(capability, facing)
    }

    /**
     * **CLIENT ONLY**
     *
     * For Rendering.
     */
    fun hasFilterClientOnly(side: EnumFacing): Boolean {
        return clientFilterFlags[side.index]
    }
}