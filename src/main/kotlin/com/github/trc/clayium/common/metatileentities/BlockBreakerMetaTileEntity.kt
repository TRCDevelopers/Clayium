package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.property.IExtendedBlockState

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "block_breaker") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/area_miner")

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    private val backingRange = Cuboid6()
    private val backingBlockPos = BlockPos.MutableBlockPos()
    override val rangeRelative: Cuboid6 get() {
        backingBlockPos.setPos(BlockPos.ORIGIN)
        backingBlockPos.move(this.frontFacing.opposite)
        return backingRange.set(backingBlockPos, backingBlockPos.add(1, 1, 1))
    }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing.opposite)
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return BlockBreakerMetaTileEntity(metaTileEntityId, tier)
    }

    override fun overlayQuads(quads: MutableList<BakedQuad>, state: IBlockState?, side: EnumFacing?, rand: Long) {
        super.overlayQuads(quads, state, side, rand)
        if (state == null || side == null || state !is IExtendedBlockState) return
        if (side == this.frontFacing.opposite) {
            quads.add(MINER_BACK[side.index])
        }
    }
}