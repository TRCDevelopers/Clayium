package com.github.trc.clayium.api.unification

import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.api.util.copyWithSize
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {

    fun registerOre(stack: ItemStack, oreDict: String) {
        OreDictionary.registerOre(oreDict, stack)
    }

    fun registerOre(stack: ItemStack, orePrefix: OrePrefix, material: IMaterial) {
        registerOre(stack, UnificationEntry(orePrefix, material).toString())
    }

    fun get(oreDict: String, stackSize: Int = 1): ItemStack {
        val ores = OreDictionary.getOres(oreDict)
        if (ores.isEmpty()) return ItemStack.EMPTY
        val stack = ores.first().copyWithSize(stackSize)
        if (!stack.hasSubtypes) stack.itemDamage = 0
        return stack
    }

    fun get(orePrefix: OrePrefix, material: IMaterial, stackSize: Int = 1): ItemStack {
        return get(UnificationEntry(orePrefix, material).toString(), stackSize)
    }

    fun getAll(oreDict: String, stackSize: Int = 1): List<ItemStack> {
        return OreDictionary.getOres(oreDict).map { it.copyWithSize(stackSize) }
    }

    fun getAll(orePrefix: OrePrefix, material: IMaterial, stackSize: Int = 1): List<ItemStack> {
        return getAll(UnificationEntry(orePrefix, material).toString(), stackSize)
    }

    fun getAll(oreDict: UnificationEntry, stackSize: Int = 1): List<ItemStack> {
        return getAll(oreDict.toString(), stackSize)
    }
}