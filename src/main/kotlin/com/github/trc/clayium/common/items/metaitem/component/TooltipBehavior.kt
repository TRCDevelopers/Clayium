package com.github.trc.clayium.common.items.metaitem.component

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.function.Consumer

class TooltipBehavior(private val tooltips: Consumer<MutableList<String>>) : IItemBehavior {
    override fun addInformation(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {
        tooltips.accept(tooltip)
    }
}
