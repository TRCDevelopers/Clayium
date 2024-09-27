package com.github.trc.clayium.api.block

import com.github.trc.clayium.api.util.ITier
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.world.World
import net.minecraftforge.common.IRarity

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

    override fun getForgeRarity(stack: ItemStack): IRarity {
        if (variantBlock is ITieredBlock) {
            return variantBlock.getTier(stack).rarity
        }
        return super.getForgeRarity(stack)
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String?>, flagIn: ITooltipFlag) {
        super.addInformation(stack, worldIn, tooltip, flagIn)
        if (variantBlock is ITieredBlock) {
            tooltip.add(ITier.tierNumericTooltip(variantBlock.getTier(stack)))
        }
    }
}