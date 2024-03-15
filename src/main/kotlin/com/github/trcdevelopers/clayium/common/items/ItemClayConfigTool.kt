package com.github.trcdevelopers.clayium.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class ItemClayConfigTool(
    maxStackSize: Int = 1,
    maxDamage: Int,
    private val type: ToolType,
    private val typeWhenSneak: ToolType? = null,
) : Item() {
    init {
        this.maxDamage = maxDamage
        this.maxStackSize = maxStackSize
    }

    override fun doesSneakBypassUse(stack: ItemStack, world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return typeWhenSneak != null
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        val typeToSend = (if (player.isSneaking) typeWhenSneak else type)
            ?: return EnumActionResult.PASS

        val block = world.getBlockState(pos).block
        val tile = world.getTileEntity(pos)
        if (block is Listener || tile is Listener) {
            (block as? Listener)?.onRightClicked(typeToSend, world, pos, player, hand, side, hitX, hitY, hitZ)
            (tile as? Listener)?.onRightClicked(typeToSend, world, pos, player, hand, side, hitX, hitY, hitZ)
            return EnumActionResult.SUCCESS
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
    }

    /**
     * can be applied to blocks or tile entities
     */
    interface Listener {
        /**
         * called when the player right-clicks a block or tile entity with the [ItemClayConfigTool]
         */
        fun onRightClicked(
            toolType: ToolType,
            world: World,
            pos: BlockPos,
            player: EntityPlayer,
            hand: EnumHand,
            facing: EnumFacing,
            hitX: Float, hitY: Float, hitZ: Float
        )
    }

    enum class ToolType {
        PIPING,
        INSERTION,
        EXTRACTION,
        ROTATION,
        FILTER_REMOVER,
    }
}