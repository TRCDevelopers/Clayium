package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.cleanroommc.modularui.utils.Alignment
import com.cleanroommc.modularui.value.sync.GuiSyncManager
import com.cleanroommc.modularui.widget.ParentWidget
import com.github.trc.clayium.api.ClayEnergy
import com.github.trc.clayium.api.capability.impl.ClayEnergyHolder
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.metatileentity.trait.ClayMarkerHandler
import com.github.trc.clayium.api.util.Cuboid6BlockPosIterator
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

class RangedMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "ranged_miner") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/miner")

    private val clayMarkerHandler = ClayMarkerHandler(this)
    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)
    val clayEnergyHolder = ClayEnergyHolder(this)

    private val posIter: Iterator<BlockPos.MutableBlockPos>? by lazy {
        val range = clayMarkerHandler.markedRangeAbsolute?.copy() ?: return@lazy null
        Cuboid6BlockPosIterator(range)
    }

    override fun getNextBlockPos(): BlockPos? {
        val iterator = posIter ?: return null
        val world = world ?: return null
        while (iterator.hasNext()) {
            val pos = iterator.next()
            if (!world.isAirBlock(pos)) {
                return pos.toImmutable()
            }
        }
        return null
    }

    override fun addProgress() {
        val r = getAccelerationRate()
        if (clayEnergyHolder.drawEnergy(CE_CONSUMPTION * r, false)) {
            progress += 100 * r
            laserEnergyHolder.drawAll()
        }
    }

    override fun buildMainParentWidget(syncManager: GuiSyncManager): ParentWidget<*> {
        return super.buildMainParentWidget(syncManager)
            .child(clayEnergyHolder.createCeTextWidget(syncManager)
                .left(0).bottom(12))
            .child(clayEnergyHolder.createSlotWidget()
                .align(Alignment.BottomRight))
    }

    // clayMarkerHandler.markedRangeAbsolute is absolute, so we need to convert it to relative.
    // However, creating a new instance every time is costly, so we use backingRange.
    private val backingRange = Cuboid6(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    override val rangeRelative: Cuboid6?
        get() {
            return clayMarkerHandler.markedRangeAbsolute?.let { backingRange.set(it).subtract(pos) }
        }

    override fun createMetaTileEntity() = RangedMinerMetaTileEntity(metaTileEntityId, tier)

    companion object {
        val CE_CONSUMPTION = ClayEnergy.milli(10)
    }
}