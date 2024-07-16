package com.github.trc.clayium.api.metatileentity.multiblock

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.INTERFACE_SYNC_MIMIC_TARGET
import com.github.trc.clayium.api.capability.ISynchronizedInterface
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.jetbrains.annotations.MustBeInvokedByOverriders

abstract class ProxyMetaTileEntityBase(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    translationKey: String,
) : MetaTileEntity(
    metaTileEntityId, tier, onlyNoneList, onlyNoneList, translationKey
), IMultiblockPart, ISynchronizedInterface {

    final override var isAttachedToMultiblock = false
        private set

    /**
     * only available on the server side.
     */
    override var target: MetaTileEntity? = null
        get() {
            if (field?.isInvalid == true) {
                field = null
                return null
            }
            val targetPos = this.targetPos ?: return null
            if (DimensionManager.getWorld(targetDimensionId)?.isBlockLoaded(targetPos) == true) {
                return field
            }
            return null
        }
        protected set(value) {
            field = value
            if (value != null) {
                this.targetPos = value.pos
                this.targetDimensionId = value.world?.provider?.dimension ?: -1
                writeTargetData(value)
            } else {
                this.targetPos = null
                this.targetDimensionId = -1
                writeTargetRemoved()
            }
        }

    final override var targetPos: BlockPos? = null
        private set
    final override var targetDimensionId: Int = -1
        private set

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setLong("targetPos", this.targetPos?.toLong() ?: -1)
        data.setInteger("targetDimensionId", this.targetDimensionId)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        // We don't set the target here because the target may not be loaded yet. It will be set at onFirstTick.
        this.targetPos = BlockPos.fromLong(data.getLong("targetPos"))
        this.targetDimensionId = data.getInteger("targetDimensionId")
    }

    override fun writeInitialSyncData(buf: PacketBuffer) {
        super.writeInitialSyncData(buf)
        val target = this.target
        if (target != null) {
            this.writeTargetData(target)
        }
    }

    override fun onFirstTick() {
        super.onFirstTick()
        if (this.targetPos != null && this.targetDimensionId != -1) {
            val world = DimensionManager.getWorld(this.targetDimensionId) ?: return
            val metaTileEntity = CUtils.getMetaTileEntity(world, this.targetPos) ?: return
            if (canLink(metaTileEntity)) {
                this.target = metaTileEntity
                this.onLink(metaTileEntity)
            }
        }
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        // no-op, this block is a proxy
    }

    final override fun addToMultiblock(controller: MultiblockControllerBase) {
        this.isAttachedToMultiblock = true
        this.target = controller
        this.onLink(controller)
    }

    final override fun removeFromMultiblock(controller: MultiblockControllerBase) {
        this.isAttachedToMultiblock = false
        this.target = null
        this.onUnlink()
    }

    final override fun synchronize(pos: BlockPos, dimensionId: Int): Boolean {
        val world = DimensionManager.getWorld(dimensionId) ?: return false
        val metaTileEntity = CUtils.getMetaTileEntity(world, pos) ?: return false
        if (!canLink(metaTileEntity)) return false

        this.target = metaTileEntity
        this.onLink(metaTileEntity)
        return true
    }

    open fun onLink(target: MetaTileEntity) { markDirty() }
    open fun onUnlink() { markDirty() }

    /**
     * called when a player attempts to establish a link using a synchronizer.
     * not called when validating the multiblock structure.
     */
    @MustBeInvokedByOverriders
    open fun canLink(target: MetaTileEntity): Boolean {
        return this.canPartShare() || !this.isAttachedToMultiblock
    }

    override fun canPartShare() = false

    override fun canOpenGui(): Boolean {
        return this.target != null
    }

    protected fun writeTargetData(target: MetaTileEntity) {
        val pos = target.pos ?: return
        val world = target.world ?: return
        writeCustomData(INTERFACE_SYNC_MIMIC_TARGET) {
            writeBoolean(true)
            writeBlockPos(pos)
            writeVarInt(world.provider.dimension)
        }
    }

    protected fun writeTargetRemoved() {
        writeCustomData(INTERFACE_SYNC_MIMIC_TARGET) {
            writeBoolean(false)
        }
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        when (discriminator) {
            INTERFACE_SYNC_MIMIC_TARGET -> {
                val hasPos = buf.readBoolean()
                if (hasPos) {
                    this.targetPos = buf.readBlockPos()
                    this.targetDimensionId = buf.readVarInt()
                } else {
                    this.targetPos = null
                    this.targetDimensionId = -1
                }
                return
            }
        }
        super.receiveCustomData(discriminator, buf)
    }

    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability === ClayiumCapabilities.SYNCHRONIZED_INTERFACE) {
            return ClayiumCapabilities.SYNCHRONIZED_INTERFACE.cast(this)
        }
        return super.getCapability(capability, facing)
    }
}