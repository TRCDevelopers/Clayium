package com.github.trc.clayium.common.metatileentities

import codechicken.lib.vec.Cuboid6
import com.github.trc.clayium.api.capability.impl.ClayiumItemStackHandler
import com.github.trc.clayium.api.metatileentity.AbstractMinerMetaTileEntity
import com.github.trc.clayium.api.metatileentity.MetaTileEntity
import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.ITier
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldServer

// メモ: 原作はブロックの右クリックではなく、アイテムの右クリックに焦点を当てていると思われる
// e.g. 火打ち石、バケツ系
class ActivatorMetaTileEntity(
    metaTileEntityId: ResourceLocation,
    tier: ITier
) : AbstractMinerMetaTileEntity(metaTileEntityId, tier, "activator", bufferValidInputModes) {

    override val itemInventory = ClayiumItemStackHandler(this, 9)
    override val rangeRelative = Cuboid6.full.copy()
    override val maxBlocksPerTick = 1

    override fun drawEnergy(accelerationRate: Double): Boolean { return true }

    override fun getNextBlockPos(): BlockPos? {
        return this.pos?.offset(this.frontFacing)
    }

    override fun mine(world: World, pos: BlockPos, state: IBlockState): Boolean {
        val pos = this.pos ?: return false
        val clickPos = getNextBlockPos() ?: return false
        val world = this.world as? WorldServer ?: return false
        val player = CUtils.getFakePlayer(world)
        player.setWorld(world)
        player.setPosition(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        player.interactionManager.processRightClickBlock(
            player, world, ItemStack.EMPTY, EnumHand.MAIN_HAND, getNextBlockPos(),
            this.frontFacing.opposite, 0.5f, 0.5f, 0.5f
        )
        return false
    }

    override fun createMetaTileEntity(): MetaTileEntity {
        return ActivatorMetaTileEntity(metaTileEntityId, tier)
    }
}