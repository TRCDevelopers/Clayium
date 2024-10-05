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
import net.minecraft.tileentity.TileEntity
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

    private var teAccess: TileEntityAccess? = null

    /**
     * only available on the server side.
     */
    override val target: MetaTileEntity?
        get() = (teAccess?.getIfLoaded() as? MetaTileEntityHolder)?.metaTileEntity

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
        val pos = BlockPos.fromLong(data.getLong("targetPos"))
        val dimId = data.getInteger("targetDimensionId")
        this.targetDimensionId = dimId
        this.targetPos = pos
        val world = DimensionManager.getWorld(dimId) ?: return
        this.teAccess = TileEntityAccess(world, pos, ::onNewTarget, ::unlinkSelf)
        // don't link here, because the target may not be loaded yet.
        // the link will be established onFirstTick.
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
                this.linkTo(metaTileEntity)
            }
        }
    }

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        // no-op, this block is a proxy
    }

    final override fun addToMultiblock(controller: MetaTileEntity) {
        this.isAttachedToMultiblock = true
        this.linkTo(controller)
    }

    final override fun removeFromMultiblock(controller: MetaTileEntity) {
        this.isAttachedToMultiblock = false
        this.unlink()
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
        this.linkTo(metaTileEntity)
        return true
    }

    private fun onNewTarget(tileEntity: TileEntity) {
        if (tileEntity !is MetaTileEntityHolder) return
        val metaTileEntity = tileEntity.metaTileEntity ?: return
        if (!canLink(metaTileEntity)) return
        this.linkTo(metaTileEntity)
    }

    @MustBeInvokedByOverriders
    open fun linkTo(target: MetaTileEntity) {
        val world = target.world ?: return
        val pos = target.pos ?: return
        this.targetPos = pos
        this.targetDimensionId = world.provider?.dimension ?: -1
        writeTargetData(target)
        teAccess = TileEntityAccess(world, pos, ::onNewTarget, ::unlinkSelf)
        markDirty()
    }

    @MustBeInvokedByOverriders
    open fun unlink() {
        writeTargetRemoved()
        markDirty()
    }
    fun unlinkSelf() {
        writeTargetRemoved()
        markDirty()
    }
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

    private fun writeTargetData(target: MetaTileEntity) {
        val pos = target.pos ?: return
        val world = target.world ?: return
        writeCustomData(INTERFACE_SYNC_MIMIC_TARGET) {
            writeBoolean(true)
            writeBlockPos(pos)
            writeVarInt(world.provider.dimension)
            writeItemStack(target.getStackForm())
        }
    }

    private fun writeTargetRemoved() {
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