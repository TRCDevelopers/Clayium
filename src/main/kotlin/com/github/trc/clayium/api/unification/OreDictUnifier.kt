package com.github.trc.clayium.api.unification

import com.github.trc.clayium.common.unification.IOreDictUnifier
import net.minecraft.item.ItemStack

object OreDictUnifier : IOreDictUnifier {

    private lateinit var delegate: IOreDictUnifier

    fun injectImpl(impl: IOreDictUnifier) {
        this.delegate = impl
    }

    override fun registerOre(stack: ItemStack, oreDict: String) {
        delegate.registerOre(stack, oreDict)
    }

    override fun getOreNames(stack: ItemStack): Set<String> {
        return delegate.getOreNames(stack)
    }

    override fun get(oreDict: String, amount: Int): ItemStack {
        return delegate.get(oreDict, amount)
    }

    override fun getAll(oreDict: String, amount: Int): List<ItemStack> {
        return delegate.getAll(oreDict, amount)
    }
}
