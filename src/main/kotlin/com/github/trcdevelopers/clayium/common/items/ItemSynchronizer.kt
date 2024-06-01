package com.github.trcdevelopers.clayium.common.items

import com.github.trcdevelopers.clayium.api.capability.ClayiumCapabilities
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

class ItemSynchronizer : Item() {
    init {
        maxStackSize = 1
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        if (player.isSneaking) return EnumActionResult.PASS
        if (world.isRemote) return EnumActionResult.SUCCESS

        val itemStack = player.getHeldItem(hand)
        val stackTag = itemStack.tagCompound
        if (stackTag != null && stackTag.hasKey("pos") && stackTag.hasKey("world")) {
            val proxy = world.getTileEntity(pos)?.getCapability(ClayiumCapabilities.SYNCHRONIZED_INTERFACE, side)
            if (proxy != null) {
                player.sendMessage(TextComponentTranslation("item.clayium.synchronizer.synchronized"))
                return EnumActionResult.SUCCESS
            }
        }

        itemStack.tagCompound = createPositionNbt(pos, world.provider.dimension)
        player.sendMessage(TextComponentTranslation("item.clayium.synchronizer.position_saved"))
        return EnumActionResult.SUCCESS
    }

    private fun createPositionNbt(pos: BlockPos, world: Int): NBTTagCompound {
        return NBTTagCompound().apply {
            setLong("pos", pos.toLong())
            setInteger("world", world)
        }
    }
}