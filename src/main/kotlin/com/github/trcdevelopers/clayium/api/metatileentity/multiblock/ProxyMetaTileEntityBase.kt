package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.capability.ClayiumDataCodecs.INTERFACE_SYNC_MIMIC_TARGET
import com.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import com.github.trcdevelopers.clayium.api.capability.ISynchronizedInterface
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import org.jetbrains.annotations.MustBeInvokedByOverriders

abstract class ProxyMetaTileEntityBase(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    validInputModes: List<MachineIoMode>, validOutputModes: List<MachineIoMode>,
    translationKey: String,
) : MetaTileEntity(
    metaTileEntityId, tier, validInputModes, validOutputModes, translationKey
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