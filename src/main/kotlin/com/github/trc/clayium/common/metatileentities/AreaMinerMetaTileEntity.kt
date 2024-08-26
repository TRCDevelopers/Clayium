package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

class AreaMinerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "area_miner") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/area_miner")

    override fun getNextBlockPos(): BlockPos? {
        TODO("Not yet implemented")
    }

    override val rangeRelative: Cuboid6
        get() = TODO("Not yet implemented")

    override fun createMetaTileEntity() = AreaMinerMetaTileEntity(metaTileEntityId, tier)
}