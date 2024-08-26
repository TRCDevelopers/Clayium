package com.github.trc.clayium.api.block

import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable

class VariantItemBlock<E, B>(val variantBlock: B) : ItemBlock(variantBlock)
where E : Enum<E>, E : IStringSerializable, B : VariantBlock<E>
{
    init {
        hasSubtypes = true
    }

    override fun getMetadata(damage: Int) = damage

    override fun getTranslationKey(stack: ItemStack): String {
        val superKey = super.getTranslationKey(stack)
        val variantName = variantBlock.getEnum(stack).getName()
        return "$superKey.$variantName"
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val superKey = super.getTranslationKey(stack)
        val variantName = variantBlock.getEnum(stack).getName()
        return I18n.format("$superKey.$variantName")
    }
}