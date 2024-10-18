package com.github.trc.clayium.common.items.metaitem.component

import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

sealed interface IItemComponent

fun interface ISubItemHandler : IItemComponent {
    fun getSubItems(itemStack: ItemStack, tab: CreativeTabs, items: NonNullList<ItemStack>)
}

interface IItemBehavior : IItemComponent {
    fun addInformation(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<String>,
        flagIn: ITooltipFlag
    ) {}
}

fun interface IItemColorHandler : IItemComponent {
    fun getColor(stack: ItemStack, tintIndex: Int): Int
}

interface IItemCapabilityProvider : IItemComponent, ICapabilityProvider {
    fun <T : Any> getCapability(capability: Capability<T>): T?

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?) =
        getCapability(capability)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
        getCapability(capability) != null
}
