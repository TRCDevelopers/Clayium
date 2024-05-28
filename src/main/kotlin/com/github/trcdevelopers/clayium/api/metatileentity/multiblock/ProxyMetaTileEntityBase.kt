package com.github.trcdevelopers.clayium.api.metatileentity.multiblock

import com.github.trcdevelopers.clayium.api.capability.ISynchronizedInterface
import com.github.trcdevelopers.clayium.api.metatileentity.MetaTileEntity
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.blocks.machine.MachineIoMode
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

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

    override fun isAttachedToMultiblock(): Boolean {
        return target != null
    }

    override fun addToMultiblock(controller: MultiblockControllerBase) {
        this.target = controller
        this.onLink(controller)
    }

    override fun removeFromMultiblock(controller: MultiblockControllerBase) {
        this.target = null
        this.onUnlink()
    }

    abstract fun onLink(target: MetaTileEntity)
    abstract fun onUnlink()

    override fun canPartShare() = false
}