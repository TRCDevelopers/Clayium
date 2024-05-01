package com.github.trcdevelopers.clayium.api.block

import com.github.trcdevelopers.clayium.api.util.CUtils
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemBlockMachine(
    block: BlockMachine,
) : ItemBlock(block) {
    init {
        hasSubtypes = true
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return when (val mte = CUtils.getMetaTileEntity(stack)) {
            null -> "unnamed"
            else -> {
                I18n.format(mte.translationKey, I18n.format("machine.clayium.tier${mte.tier}"))
            }
        }
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        return CUtils.getMetaTileEntity(stack)?.addInformation(stack, worldIn, tooltip, flagIn)
            ?: super.addInformation(stack, worldIn, tooltip, flagIn)
    }
}