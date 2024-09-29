package com.github.trc.clayium.api.metatileentity.multiblock

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.INTERFACE_SYNC_MIMIC_TARGET
import com.github.trc.clayium.api.capability.ISynchronizedInterface
import com.github.trc.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntityHolder
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.TileEntityAccess
import com.github.trc.clayium.api.util.getMetaTileEntity
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

    private var backingTarget: TileEntityAccess? = null

    /**
     * only available on the server side.
     */
    override var target: MetaTileEntity?
        get() = (backingTarget?.getIfLoaded() as? MetaTileEntityHolder)?.metaTileEntity
        protected set(value) {
            if (value == null) {
                this.targetPos = null
                this.targetDimensionId = -1
                writeTargetRemoved()
                backingTarget = null
            } else {
                val world = value.world ?: return
                val pos = value.pos ?: return
                this.targetPos = pos
                this.targetDimensionId = world.provider?.dimension ?: -1
                writeTargetData(value)
                backingTarget = TileEntityAccess(world, pos)
            }
        }

    final override var targetPos: BlockPos? = null
        private set
    final override var targetDimensionId: Int = -1
        private set
    final override var targetItemStack: ItemStack = ItemStack.EMPTY

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
        if (this.targetPos != null) {
            val world = DimensionManager.getWorld(this.targetDimensionId) ?: return
            val metaTileEntity = world.getMetaTileEntity(this.targetPos) ?: return
            if (canLink(metaTileEntity)) {
                this.target = metaTileEntity
                this.onLink(metaTileEntity)
            }
        }
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        // no-op, this block is a proxy
    }

    final override fun addToMultiblock(controller: MetaTileEntity) {
        this.isAttachedToMultiblock = true
        this.target = controller
        this.onLink(controller)
    }

    final override fun removeFromMultiblock(controller: MetaTileEntity) {
        this.isAttachedToMultiblock = false
        this.target = null
        this.onUnlink()
    }

    /**
     * check if synchronization with a Synchronizer Item is allowed.
     */
    protected open fun canSynchronize() = true

    final override fun synchronize(pos: BlockPos, dimensionId: Int): Boolean {
        if (!canSynchronize()) return false
        val world = DimensionManager.getWorld(dimensionId) ?: return false
        val metaTileEntity = world.getMetaTileEntity(pos) ?: return false
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
            writeItemStack(target.getStackForm())
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
                    this.targetItemStack = buf.readItemStack()
                } else {
                    this.targetPos = null
                    this.targetDimensionId = -1
                    this.targetItemStack = ItemStack.EMPTY
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