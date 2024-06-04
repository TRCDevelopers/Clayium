package com.github.trcdevelopers.clayium.common.blocks.clay

import com.github.trcdevelopers.clayium.api.item.ITieredItem
import com.github.trcdevelopers.clayium.api.util.ClayTiers
import com.github.trcdevelopers.clayium.api.util.ITier
import com.github.trcdevelopers.clayium.common.Clayium
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemBlockCompressedClay(block: BlockCompressedClay) : ItemBlock(block), ITieredItem {
    init {
        hasSubtypes = true
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        return I18n.format("tile.${Clayium.MOD_ID}.compressed_clay_tier${stack.metadata}.name")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.tier", stack.metadata))
        tooltip.add(I18n.format("tooltip.${Clayium.MOD_ID}.ce", 0))
    }

    override fun getTier(stack: ItemStack): ITier {
        return ClayTiers.entries[stack.metadata]
    }
}