package com.github.trc.clayium.api.metatileentity

import com.github.trc.clayium.api.ClayiumApi
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.INITIALIZE_MTE
import com.github.trc.clayium.api.util.CLog
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants.NBT
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class MetaTileEntityHolder : NeighborCacheTileEntityBase(), ITickable {
    var metaTileEntity: MetaTileEntity? = null
        set(sampleMetaTileEntity) {
            sampleMetaTileEntity?.holder = this
            field = sampleMetaTileEntity
        }

    fun setMetaTileEntityFromSample(sampleMetaTileEntity: MetaTileEntity): MetaTileEntity {
        val newMetaTileEntity = sampleMetaTileEntity.createMetaTileEntity()
        this.metaTileEntity = newMetaTileEntity
        val mteId = sampleMetaTileEntity.metaTileEntityId
        val registry = ClayiumApi.mteManager.getRegistry(mteId.namespace)
        if (world != null && !world.isRemote) {
            writeCustomData(INITIALIZE_MTE) {
                writeVarInt(registry.networkId)
                writeVarInt(registry.getIdByKey(sampleMetaTileEntity.metaTileEntityId))
                newMetaTileEntity.writeInitialSyncData(this)
            }
            world.neighborChanged(pos, blockType, pos)
            markDirty()
        }
        return newMetaTileEntity
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(compound)
        metaTileEntity?.let { mte ->
            compound.setString("metaId", mte.metaTileEntityId.toString())
            compound.setTag("metaTileEntityData", NBTTagCompound().apply { mte.writeToNBT(this) })
        }
        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)
        if (compound.hasKey("metaId", NBT.TAG_STRING)) {
            val mteId = ResourceLocation(compound.getString("metaId"))
            val registry = ClayiumApi.mteManager.getRegistry(mteId.namespace)
            registry.getObject(mteId)?.let { sampleMte ->
                val newMte = sampleMte.createMetaTileEntity()
                newMte.readFromNBT(compound.getCompoundTag("metaTileEntityData"))
                this.metaTileEntity = newMte
            } ?: CLog.error("Failed to load MetaTileEntity with invalid id: $mteId")
        }
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        metaTileEntity?.let { mte ->
            val mteId = mte.metaTileEntityId
            val registry = ClayiumApi.mteManager.getRegistry(mteId.namespace)
            buf.writeBoolean(true)
            buf.writeVarInt(registry.networkId)
            buf.writeVarInt(registry.getIdByKey(mteId))
            mte.writeInitialSyncData(buf)
        } ?: buf.writeBoolean(false)
    }

    override fun receiveInitialSyncData(buf: PacketBuffer) {
        if (buf.readBoolean()) {
            receiveMteInitializationData(buf)
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
        val registry = ClayiumApi.mteManager.getRegistry(buf.readVarInt())
        val sampleMetaTileEntity = registry.getObjectById(buf.readVarInt()) ?: return
        val newMetaTileEntity = this.setMetaTileEntityFromSample(sampleMetaTileEntity)
        newMetaTileEntity.receiveInitialSyncData(buf)
        scheduleRenderUpdate()
    }

    override fun update() {
        metaTileEntity?.update()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        return metaTileEntity?.getCapability(capability, facing) != null ||
            super.hasCapability(capability, facing)
    }

    override fun <T> getCapability(capability: Capability<T?>, facing: EnumFacing?): T? {
        return metaTileEntity?.getCapability(capability, facing)
            ?: super.getCapability(capability, facing)
    }

    override fun onNeighborChanged(facing: EnumFacing) {
        super.onNeighborChanged(facing)
        metaTileEntity?.onNeighborChanged(facing)
    }

    fun neighborChanged() {
        metaTileEntity?.neighborChanged()
    }

    fun scheduleRenderUpdate() {
        world?.markBlockRangeForRenderUpdate(pos, pos)
    }

    fun notifyNeighbors() {
        world?.notifyNeighborsOfStateChange(pos, blockType, true)
    }

    override fun shouldRefresh(
        world: World,
        pos: BlockPos,
        oldState: IBlockState,
        newSate: IBlockState
    ): Boolean {
        return oldState.block != newSate.block
    }

    @SideOnly(Side.CLIENT)
    override fun shouldRenderInPass(pass: Int) =
        metaTileEntity?.shouldRenderInPass(pass) ?: super.shouldRenderInPass(pass)

    @Suppress(
        "UsePropertyAccessSyntax"
    ) // `super.maxRenderDistanceSquared` errors with "Unresolved reference"
    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared() =
        metaTileEntity?.getMaxRenderDistanceSquared() ?: super.getMaxRenderDistanceSquared()

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox() =
        metaTileEntity?.getRenderBoundingBox() ?: super.getRenderBoundingBox()
}
