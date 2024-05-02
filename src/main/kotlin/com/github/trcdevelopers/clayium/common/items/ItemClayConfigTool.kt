package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.api.util.CUtils
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

        CUtils.getMetaTileEntity(world, pos)?.onToolClick(typeToSend, player, hand, side, hitX, hitY, hitZ)
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand)
    }

    enum class ToolType {
        PIPING,
        INSERTION,
        EXTRACTION,
        ROTATION,
        FILTER_REMOVER,
    }
}