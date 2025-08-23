package io.github.trcdevelopers.clayium.api.unification

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import io.github.trcdevelopers.clayium.api.util.CLog
import io.github.trcdevelopers.clayium.common.unification.IOreDictUnifier
import net.minecraft.item.ItemStack

// TODO: implement directory. Clayium's items should be preferred over other mods' items.
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

    override fun has(stack: ItemStack, oreDict: String): Boolean {
        return delegate.has(stack, oreDict)
    }

    fun registerAdditionalOreDict(oreDict: String, additionalOreDict: String) {
        val stack = this.get(oreDict)
        if (stack.isEmpty) {
            CLog.error("Ore {} is empty. You should register it before using this method." , oreDict)
        }
        this.registerOre(stack, oreDict)
    }

    fun registerAdditionalOreDict(orePrefix: OrePrefix, material: IMaterial, additionalOreDict: String) {
        this.registerAdditionalOreDict(UnificationEntry(orePrefix, material).toString(), additionalOreDict)
    }
}