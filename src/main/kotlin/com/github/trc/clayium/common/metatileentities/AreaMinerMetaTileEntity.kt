package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.ClayMarkerHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

//todo
class AreaMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "area_miner") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/area_miner")

    private val clayMarkerHandler = ClayMarkerHandler(this)

    override fun getNextBlockPos(): BlockPos? {
        return this.pos!!.offset(this.frontFacing)
    }

    // clayMarkerHandler.markedRangeAbsolute is absolute, so we need to convert it to relative.
    // However, creating a new instance every time is costly, so we use backingRange.
    private val backingRange = Cuboid6(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
    override val rangeRelative: Cuboid6?
        get() {
            return clayMarkerHandler.markedRangeAbsolute?.let { backingRange.set(it).subtract(pos) }
        }

    override fun createMetaTileEntity() = AreaMinerMetaTileEntity(metaTileEntityId, tier)
}