package com.github.trc.clayium.api.block

import com.github.trc.clayium.api.util.CUtils
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.IRarity

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
                mte.getItemStackDisplayName()
            }
        }
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        return CUtils.getMetaTileEntity(stack)?.addInformation(stack, worldIn, tooltip, flagIn)
            ?: super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    override fun getForgeRarity(stack: ItemStack): IRarity {
        return CUtils.getMetaTileEntity(stack)?.forgeRarity
            ?: super.getForgeRarity(stack)
    }
}