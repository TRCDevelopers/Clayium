package com.github.trcdevelopers.clayium.common.items.metaitem.component

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.world.World

sealed interface IItemComponent

interface ISubItemHandler : IItemComponent {
    fun getSubItems(itemStack: ItemStack, tab: CreativeTabs, items: NonNullList<ItemStack>)
}

interface IItemBehavior : IItemComponent {
    fun addInformation(stack: ItemStack, world: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {}
}

interface IItemColorHandler : IItemComponent {
    fun getColor(stack: ItemStack, tintIndex: Int): Int
}
