package io.github.trcdevelopers.clayium.common.unification

import io.github.trcdevelopers.clayium.api.unification.material.IMaterial
import io.github.trcdevelopers.clayium.api.unification.ore.OrePrefix
import io.github.trcdevelopers.clayium.api.unification.stack.UnificationEntry
import net.minecraft.item.ItemStack

interface IOreDictUnifier {
    fun registerOre(stack: ItemStack, oreDict: String)
    fun registerOre(stack: ItemStack, orePrefix: OrePrefix, material: IMaterial) = registerOre(stack, UnificationEntry(orePrefix, material).toString())

    fun getOreNames(stack: ItemStack): Set<String>

    fun get(oreDict: String, amount: Int = 1): ItemStack
    fun get(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = get(UnificationEntry(orePrefix, material).toString(), amount)

    fun getAll(oreDict: String, amount: Int = 1): List<ItemStack>
    fun getAll(orePrefix: OrePrefix, material: IMaterial, amount: Int = 1) = getAll(UnificationEntry(orePrefix, material), amount)
    fun getAll(oreDict: UnificationEntry, amount: Int = 1) = getAll(oreDict.toString(), amount)

    fun has(stack: ItemStack, oreDict: String): Boolean

    fun exists(oreDict: String): Boolean = !get(oreDict).isEmpty
    fun exists(orePrefix: OrePrefix, material: IMaterial): Boolean = exists(UnificationEntry(orePrefix, material).toString())
}