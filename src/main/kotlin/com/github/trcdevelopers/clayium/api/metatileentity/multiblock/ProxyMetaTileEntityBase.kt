package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.INTERFACE_SYNC_MIMIC_TARGET
import com.github.trcdevelopers.clayium.api.capability.ISynchronizedInterface
import com.github.trcdevelopers.clayium.api.capability.impl.EmptyItemStackHandler
import com.github.trcdevelopers.clayium.api.metatileentity.AutoIoHandler
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.CUtils
import com.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.item.ItemStack
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

    /**
     * only available on the server side.
     */
    override var target: MetaTileEntity? = null
        protected set

    override var targetPos: BlockPos? = null
        protected set

    override var targetDimensionId: Int = -1
        protected set

    override val importItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val exportItems: IItemHandlerModifiable = EmptyItemStackHandler
    override val itemInventory: IItemHandler = EmptyItemStackHandler
    override val autoIoHandler: AutoIoHandler = AutoIoHandler.Empty(this)

    override fun clearMachineInventory(itemBuffer: MutableList<ItemStack>) {
        // no-op, this block is a proxy
    }

    override fun isAttachedToMultiblock(): Boolean {
        return target != null
    }

    final override fun addToMultiblock(controller: MultiblockControllerBase) {
        if (!canLink(controller)) return
        this.target = controller
        this.onLink(controller)
    }

    final override fun removeFromMultiblock(controller: MultiblockControllerBase) {
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

    @MustBeInvokedByOverriders
    open fun onLink(target: MetaTileEntity) {
        writeTargetData(target)
    }

    @MustBeInvokedByOverriders
    open fun onUnlink() {
        writeTargetRemoved()
    }

    open fun canLink(target: MetaTileEntity): Boolean = true

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
        return super.getCapability(capability, facing)
    }
}