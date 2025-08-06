package io.github.trcdevelopers.clayium.common.metatileentities

import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.PanelSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import io.github.trcdevelopers.clayium.api.ClayEnergy
import io.github.trcdevelopers.clayium.api.capability.impl.ClayEnergyHolder
import io.github.trcdevelopers.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import io.github.trcdevelopers.clayium.api.metatileentity.MteRenderingConfig
import io.github.trcdevelopers.clayium.api.metatileentity.trait.AutoIoHandler
import io.github.trcdevelopers.clayium.api.metatileentity.trait.ClayMarkerHandler
import io.github.trcdevelopers.clayium.api.util.BlockPosIterator
import io.github.trcdevelopers.clayium.api.util.ITier
import io.github.trcdevelopers.clayium.api.util.MachineIoMode
import io.github.trcdevelopers.clayium.api.util.clayiumId
import io.github.trcdevelopers.clayium.common.config.ConfigCore
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

open class RangedMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier,
    machineName: String = "ranged_miner",
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, machineName, renderMinerBack = false) {

    private val clayMarkerHandler = ClayMarkerHandler(this)
    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)
    val clayEnergyHolder = ClayEnergyHolder(this)

    override val maxBlocksPerTick: Int = ConfigCore.misc.rangedMachineMaxBlocksPerTick

    private val posIter by lazy {
        val range = clayMarkerHandler.markedRangeAbsolute?.copy() ?: return@lazy null
        BlockPosIterator(range)
    }

    override fun getNextBlockPos(): BlockPos? {
        val iterator = posIter ?: return null
        val world = world ?: return null
        while (iterator.hasNext()) {
            val pos = iterator.next()
            if (!world.isAirBlock(pos)) {
                return pos
            }
        }
        if (this.repeatEnabled) {
            iterator.restart()
            return if (iterator.hasNext()) iterator.next() else null
        }
        return null
    }

    override fun drawEnergy(accelerationRate: Double): Boolean {
        return clayEnergyHolder.drawEnergy(CE_CONSUMPTION * getAccelerationRate(), false)
    }

    override fun buildMainParentWidget(syncManager: PanelSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .left(0).bottom(12))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
    }

    override fun onPlacement() {
        this.setInput(EnumFacing.UP, MachineIoMode.CE)
        super.onPlacement()
    }

    override val rangeRelativeClient get() = clayMarkerHandler.renderingRangeRelative

    override fun createMetaTileEntity() = RangedMinerMetaTileEntity(metaTileEntityId, tier)

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/miner"))
    }

    companion object {
        val CE_CONSUMPTION = ClayEnergy.milli(10)
    }
}