package com.github.trc.clayium.api.unification

import com.github.trc.clayium.api.unification.material.IMaterial
import com.github.trc.clayium.api.unification.ore.OrePrefix
import com.github.trc.clayium.api.unification.stack.MultiItemVariantMap
import com.github.trc.clayium.api.unification.stack.MutableItemVariantMap
import com.github.trc.clayium.api.unification.stack.SingleItemVariantMap
import com.github.trc.clayium.api.unification.stack.UnificationEntry
import com.github.trc.clayium.api.util.copyWithSize
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.oredict.OreDictionary

object OreDictUnifier {

    private val item2OreNames = mutableMapOf<Item, MutableItemVariantMap<MutableSet<String>>>()

    fun initialize() {
        OreDictionary.getOreNames().forEach { oreName ->
            OreDictionary.getOres(oreName).forEach { stack ->
                onOreRegistration(OreDictionary.OreRegisterEvent(oreName, stack))
            }
        }
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onOreRegistration(e: OreDictionary.OreRegisterEvent) {
        val oreName = e.name
        val stack = e.ore
        val item = stack.item
        val meta = stack.itemDamage

        val variantMap = item2OreNames.computeIfAbsent(item) {
            if (stack.hasSubtypes) MultiItemVariantMap() else SingleItemVariantMap()
        }
        val names = variantMap.computeIfAbsent(meta.toShort()) { mutableSetOf() }
        names.add(oreName)
        //todo
    }

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

    fun exists(orePrefix: OrePrefix, material: IMaterial): Boolean {
        return !get(orePrefix, material).isEmpty
    }
}