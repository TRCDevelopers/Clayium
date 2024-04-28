package com.github.trcdevelopers.clayium.api.metatileentity

import com.github.trcdevelopers.clayium.api.ClayiumApi
import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.INITIALIZE_MTE
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants.NBT

class MetaTileEntityHolder : NeighborCacheTileEntityBase() {
    var metaTileEntity: MetaTileEntity? = null
        set(sampleMetaTileEntity) {
            if (sampleMetaTileEntity == null) {
                field = null
                return
            }
            field = sampleMetaTileEntity.createMetaTileEntity().also {
                it.holder = this
            }
        }

    fun setAndInitMetaTileEntity(sampleMetaTileEntity: MetaTileEntity): MetaTileEntity {
        val mte = sampleMetaTileEntity.createMetaTileEntity()
        metaTileEntity = mte
        if (world?.isRemote == false) {
            writeCustomData(INITIALIZE_MTE) {
                writeInt(ClayiumApi.MTE_REGISTRY.getIdByKey(sampleMetaTileEntity.metaTileEntityId))
                mte.writeInitialSyncData(this)
            }
            world.neighborChanged(pos, blockType, pos)
            markDirty()
        }
        return mte
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        metaTileEntity?.let { mte ->
            compound.setString("metaId", mte.metaTileEntityId.toString())
            compound.setTag("metaTileEntityData", NBTTagCompound().also { mte.writeToNBT(it) })
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("metaId", NBT.TAG_STRING)) {
            val mteId = ResourceLocation(compound.getString("metaId"))
            ClayiumApi.MTE_REGISTRY.getObject(mteId)?.let { sampleMte ->
                metaTileEntity = sampleMte.createMetaTileEntity().also { it.readFromNBT(compound.getCompoundTag("metaTileEntityData")) }
            }
                ?: Clayium.LOGGER.error("Failed to load MetaTileEntity with invalid id: $mteId")
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        metaTileEntity?.let {
            buf.writeBoolean(true)
            buf.writeInt(ClayiumApi.MTE_REGISTRY.getIdByKey(it.metaTileEntityId))
            it.writeInitialSyncData(buf)
        } ?: buf.writeBoolean(false)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        if (buf.readBoolean()) {
            ClayiumApi.MTE_REGISTRY.getObjectById(buf.readInt())?.also {
                it.receiveInitialSyncData(buf)
            }
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        if (discriminator == INITIALIZE_MTE) {
            receiveMteInitializationData(buf)
        } else {
            metaTileEntity?.receiveCustomData(discriminator, buf)
        }
    }

    private fun receiveMteInitializationData(buf: PacketBuffer) {
        val mteId = buf.readVarInt()
        setAndInitMetaTileEntity(ClayiumApi.MTE_REGISTRY.getObjectById(mteId) ?: return).also {
            it.receiveInitialSyncData(buf)
            scheduleRenderUpdate()
        }
    }

    private fun scheduleRenderUpdate() {
        world?.markBlockRangeForRenderUpdate(pos, pos)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block != newSate.block
    }
}