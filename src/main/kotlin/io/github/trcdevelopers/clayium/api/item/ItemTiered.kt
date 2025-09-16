package io.github.trcdevelopers.clayium.api.item

import io.github.trcdevelopers.clayium.api.util.ITier
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.IRarity

abstract class ItemTiered : Item(), ITieredItem {
    override fun getForgeRarity(stack: ItemStack): IRarity {
        return this.getTier(stack).rarity
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        tooltip.add("Tier ${this.getTier(stack).numeric}")
    }
}