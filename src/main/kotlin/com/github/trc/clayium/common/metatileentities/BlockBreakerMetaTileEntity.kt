package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.CValues
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.metatileentity.trait.AutoIoHandler
import com.github.trc.clayium.api.util.ITier
import com.github.trc.clayium.api.util.clayiumId
import com.github.trc.clayium.common.util.TransferUtils
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.property.IExtendedBlockState

class BlockBreakerMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "block_breaker") {

    override val faceTexture: ResourceLocation = clayiumId("blocks/miner")

    @Suppress("unused")
    val ioHandler = AutoIoHandler.Exporter(this)

    private val backingRange = Cuboid6()
    private val backingBlockPos = BlockPos.MutableBlockPos()
    override val rangeRelative: Cuboid6 get() {
        backingBlockPos.setPos(BlockPos.ORIGIN)
        backingBlockPos.move(this.frontFacing.opposite)
        return backingRange.set(backingBlockPos, backingBlockPos.add(1, 1, 1))
    }

    override fun mineBlocks() {
        val world = this.world ?: return
        val targetPos = this.pos?.offset(this.frontFacing.opposite) ?: return

        val state = world.getBlockState(targetPos)
        val hardness = state.getBlockHardness(world, targetPos)

        if (hardness == CValues.HARDNESS_UNBREAKABLE) {
            return
        }

        val requiredProgress = getRequiredProgress(hardness)
        if (progress < requiredProgress) {
            addProgress()
        }
        if (progress >= requiredProgress) {
            progress = 0.0
            val drops = NonNullList.create<ItemStack>()
            state.block.getDrops(drops, world, targetPos, state, 0)
            if (TransferUtils.insertToHandler(itemInventory, drops, true)) {
                TransferUtils.insertToHandler(itemInventory, drops, false)
                world.destroyBlock(targetPos, false)
            }
        }
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