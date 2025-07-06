package com.github.trc.clayium.common.items

import com.github.trc.clayium.common.util.RayTraceMemory
import com.github.trc.clayium.common.util.UtilLocale
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
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
        println("Direction Memory: $rayTraceMemory")

        player.sendMessage(TextComponentTranslation("item.clayium.direction_memory.saved"))
        return EnumActionResult.PASS
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        UtilLocale.formatTooltips(tooltip, "${this.translationKey}.tooltip")
    }
}