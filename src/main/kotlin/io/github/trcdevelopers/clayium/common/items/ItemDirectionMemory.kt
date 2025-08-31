package io.github.trcdevelopers.clayium.common.items

import io.github.trcdevelopers.clayium.api.capability.ClayiumTileCapabilities
import io.github.trcdevelopers.clayium.common.util.RayTraceMemory
import io.github.trcdevelopers.clayium.common.util.UtilLocale
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

class ItemDirectionMemory : Item() {
    init {
        maxStackSize = 1
    }

    override fun onItemUseFirst(player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, hand: EnumHand): EnumActionResult {
        if (world.isRemote) return EnumActionResult.PASS

        if (tryApplyDirectionMemory(player, hand, world, pos, side)) {
            player.sendMessage(TextComponentTranslation("item.clayium.direction_memory.applied"))
            return EnumActionResult.SUCCESS
        }
        return EnumActionResult.PASS
    }

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (worldIn.isRemote) return EnumActionResult.PASS

        val eyeHeight = player.eyeHeight
        val playerVector = player.positionVector.add(0.0, eyeHeight.toDouble(), 0.0)
        val hitVec = Vec3d((pos.x + hitX).toDouble(), (pos.y + hitY).toDouble(), (pos.z + hitZ).toDouble())
        val rayTraceMemory = RayTraceMemory(
            entityEyePosAbsolute = playerVector,
            hitPositionAbsolute = hitVec,
            side = facing
        )
        val stack = player.getHeldItem(hand)
        val stackTag = stack.tagCompound ?: NBTTagCompound()
        val memoryNbt = rayTraceMemory.serializeNBT()
        stackTag.setTag("direction_memory", memoryNbt)
        stack.tagCompound = stackTag

        player.sendMessage(TextComponentTranslation("item.clayium.direction_memory.saved"))
        return EnumActionResult.PASS
    }

    private fun tryApplyDirectionMemory(player: EntityPlayer, hand: EnumHand, world: World, pos: BlockPos, facing: EnumFacing): Boolean {
        if (world.isRemote) return false
        val memory = RayTraceMemory.from(player.getHeldItem(hand).tagCompound?.getCompoundTag("direction_memory"))
            ?: return false
        return world.getTileEntity(pos)
            ?.getCapability(ClayiumTileCapabilities.RAY_TRACE_MEMORY_APPLICABLE, facing)
            ?.acceptRayTraceMemory(memory) ?: false
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        UtilLocale.formatTooltips(tooltip, "${this.translationKey}.tooltip")
    }
}