package com.github.trc.clayium.api.block

import com.github.trc.clayium.api.util.CUtils
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.util.KeyInput
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.IRarity
import net.minecraftforge.common.util.Constants

class ItemBlockMachine(
    block: BlockMachine,
) : ItemBlock(block) {
    init {
        hasSubtypes = true
    }

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult {
        if (!world.isRemote && KeyInput.SPRINT.isKeyDown(player as EntityPlayerMP)) {
            val stack = player.getHeldItem(hand)
            val sampleMetaTileEntity =
                CUtils.getMetaTileEntity(stack) ?: return EnumActionResult.PASS
            val metaTileEntity = world.getMetaTileEntity(pos) ?: return EnumActionResult.PASS
            if (!metaTileEntity.canBeReplacedTo(world, pos, sampleMetaTileEntity))
                return EnumActionResult.PASS
            val oldState = world.getBlockState(pos)
            if (!player.isCreative) stack.shrink(1)

            world.playEvent(
                Constants.WorldEvents.BREAK_BLOCK_EFFECTS,
                pos,
                Block.getStateId(oldState)
            )

            metaTileEntity.replaceTo(world, pos, sampleMetaTileEntity)
            return EnumActionResult.SUCCESS
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return when (val mte = CUtils.getMetaTileEntity(stack)) {
            null -> "unnamed"
            else -> {
                mte.getItemStackDisplayName()
            }
        }
    }

    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        return CUtils.getMetaTileEntity(stack)?.addInformation(stack, worldIn, tooltip, flagIn)
            ?: super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return CUtils.getMetaTileEntity(stack)?.forgeRarity ?: super.getForgeRarity(stack)
    }
}
