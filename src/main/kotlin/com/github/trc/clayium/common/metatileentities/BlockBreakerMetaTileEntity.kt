package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.capability.ClayiumDataCodecs.UPDATE_FRONT_FACING
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.MteRenderingConfig
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.network.PacketBuffer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "block_breaker", renderMinerBack = true) {

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    override val maxBlocksPerTick: Int = 1

    override var rangeRelativeClient: Cuboid6 = Cuboid6.full.copy().add(BlockPos.ORIGIN.offset(this.frontFacing.opposite))

    override fun drawEnergy(accelerationRate: Double): Boolean {
        // no energy required
        return true
    }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun isFacingValid(facing: EnumFacing): Boolean {
        return true
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return BlockBreakerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun receiveCustomData(discriminator: Int, buf: PacketBuffer) {
        super.receiveCustomData(discriminator, buf)
        if (discriminator == UPDATE_FRONT_FACING) {
            this.rangeRelativeClient = Cuboid6.full.copy().add(BlockPos.ORIGIN.offset(this.frontFacing.opposite))
        }
    }

    override val renderingConfig by lazy {
        MteRenderingConfig.face(clayiumId("blocks/miner"))
    }
}