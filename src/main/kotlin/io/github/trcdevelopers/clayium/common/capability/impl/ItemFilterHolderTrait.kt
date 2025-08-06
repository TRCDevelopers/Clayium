package io.github.trcdevelopers.clayium.common.capability.impl

import io.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.UPDATE_FILTER
import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.api.capability.IItemFilter
import io.github.trcdevelopers.clayium.api.capability.IItemFilterApplicatable
import io.github.trcdevelopers.clayium.api.metatileentity.MTETrait
import io.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.items.filter.ItemFilterBase
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.registry.ForgeRegistries

class ItemFilterHolderTrait(mte: MetaTileEntity) : MTETrait(mte, clayiumId("item_filter_holder").toString()), IItemFilterApplicatable {

    private val filters = MutableList<IItemFilter?>(6) { null }
    private val filterData = MutableList<Pair<ItemFilterBase, NBTTagCompound?>?>(6) { null }
    private val clientFilterFlags = BooleanArray(6) { false }

    override fun setFilter(side: EnumFacing, filter: IItemFilter, filterItem: ItemFilterBase, stackTag: NBTTagCompound?) {
        filters[side.index] = filter
        filterData[side.index] = Pair(filterItem, stackTag)
        metaTileEntity.markAsDirty()
        writeCustomData(UPDATE_FILTER) {
            writeVarInt(side.index)
            writeBoolean(true)
        }
    }

    override fun getFilter(side: EnumFacing): IItemFilter? {
        return filters[side.index]
    }

    override fun createFilterStack(side: EnumFacing): ItemStack? {
        val data = filterData[side.index] ?: return null
        val stack = ItemStack(data.first).apply { tagCompound = data.second?.copy() }
        return stack
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
        for ((i, p) in filterData.withIndex()) {
            if (p == null) continue
            val filterItem = p.first
            val filterTag = p.second
            val filterRegName = filterItem.registryName ?: continue
            if (filterTag != null) data.setTag("filterTag$i", filterTag)
            data.setString("filterItemRegistryName$i", filterRegName.toString())
        }
        return data
    }

    override fun deserializeNBT(data: NBTTagCompound) {
        for (i in 0..<6) {
            if (!data.hasKey("filterItemRegistryName$i", Constants.NBT.TAG_STRING)) continue
            val filterItemRegistryName = data.getString("filterItemRegistryName$i")
            val filterItem = ForgeRegistries.ITEMS.getValue(ResourceLocation(filterItemRegistryName))
            if (filterItem == null) {
                CLog.warn("Item Filter $filterItemRegistryName not found. pos: ${metaTileEntity.pos}, side: ${EnumFacing.byIndex(i)}")
                continue
            } else if (filterItem !is ItemFilterBase) {
                CLog.warn("Item Filter is corrupted. id: $filterItemRegistryName, pos: ${metaTileEntity.pos}, side: ${EnumFacing.byIndex(i)}")
                continue
            }

            val filterTag = data.getCompoundTag("filterTag$i")
            val stack = ItemStack(filterItem).apply { tagCompound = filterTag }
            this.filters[i] = filterItem.createItemFilter(stack)
            this.filterData[i] = Pair(filterItem, filterTag)
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