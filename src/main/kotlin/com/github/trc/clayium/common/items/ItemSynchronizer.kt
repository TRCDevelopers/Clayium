package com.github.trc.clayium.common.items

import com.github.trc.clayium.api.capability.ClayiumCapabilities
import com.github.trc.clayium.api.util.getMetaTileEntity
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.DimensionManager

class ItemSynchronizer : Item() {
    init {
        maxStackSize = 1
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        if (world.getMetaTileEntity(pos) == null || player.isSneaking) return EnumActionResult.PASS
        if (world.isRemote) return EnumActionResult.SUCCESS

        val itemStack = player.getHeldItem(hand)
        val stackTag = itemStack.tagCompound
        if (stackTag != null && isTagValid(stackTag)) {
            val proxy = world.getTileEntity(pos)?.getCapability(ClayiumCapabilities.SYNCHRONIZED_INTERFACE, side)
            if (proxy != null) {
                val targetPos = BlockPos.fromLong(stackTag.getLong("pos"))
                val targetWorld = stackTag.getInteger("world")
                if (proxy.synchronize(targetPos, targetWorld)) {
                    player.sendMessage(TextComponentTranslation("item.clayium.synchronizer.synchronized", createPosTooltip(targetPos, targetWorld)))
                } else {
                    player.sendMessage(TextComponentTranslation("item.clayium.synchronizer.synchronize_failed", createPosTooltip(targetPos, targetWorld)))
                }
                return EnumActionResult.SUCCESS
            }
        }

        itemStack.tagCompound = createPositionNbt(pos, world.provider.dimension)
        player.sendMessage(TextComponentTranslation("item.clayium.synchronizer.position_saved", createPosTooltip(pos, world.provider.dimension)))

        return EnumActionResult.SUCCESS
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val stackTag = stack.tagCompound
        if (stackTag != null && stackTag.hasKey("pos") && stackTag.hasKey("world")) {
            val pos = BlockPos.fromLong(stackTag.getLong("pos"))
            val world = stackTag.getInteger("world")
            tooltip.add(createPosTooltip(pos, world))
        }
        tooltip.add("")
        UtilLocale.formatTooltips(tooltip, "item.clayium.synchronizer.tooltip")
    }

    private fun isTagValid(tag: NBTTagCompound): Boolean {
        return tag.hasKey("pos") && tag.hasKey("world")
    }

    private fun createPositionNbt(pos: BlockPos, dimensionId: Int): NBTTagCompound {
        return NBTTagCompound().apply {
            setLong("pos", pos.toLong())
            setInteger("world", dimensionId)
        }
    }

    private fun createPosTooltip(pos: BlockPos, dimensionId: Int): String {
        return "(${DimensionManager.getWorld(dimensionId).provider.dimensionType.name} : ${pos.x}, ${pos.y}, ${pos.z})"
    }
}