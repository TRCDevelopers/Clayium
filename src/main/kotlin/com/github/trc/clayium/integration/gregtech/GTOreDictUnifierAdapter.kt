package com.github.trc.clayium.integration.gregtech

import com.github.trc.clayium.api.util.copyWithSize
import com.github.trc.clayium.common.unification.IOreDictUnifier
import net.minecraft.item.ItemStack

object GTOreDictUnifierAdapter : IOreDictUnifier {
    override fun registerOre(stack: ItemStack, oreDict: String) {
        gregtech.api.unification.OreDictUnifier.registerOre(stack, oreDict)
    }

    override fun getOreNames(stack: ItemStack): Set<String> {
        return gregtech.api.unification.OreDictUnifier.getOreDictionaryNames(stack)
    }

    override fun get(oreDict: String, amount: Int): ItemStack {
        return gregtech.api.unification.OreDictUnifier.get(oreDict).copyWithSize(amount)
    }

    override fun getAll(oreDict: String, amount: Int): List<ItemStack> {
        return gregtech.api.unification.OreDictUnifier.getAllWithOreDictionaryName(oreDict)
    }
}
